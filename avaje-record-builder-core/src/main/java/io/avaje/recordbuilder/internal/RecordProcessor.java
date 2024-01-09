package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.createSourceFile;
import static io.avaje.recordbuilder.internal.APContext.elements;
import static io.avaje.recordbuilder.internal.APContext.getModuleInfoReader;
import static io.avaje.recordbuilder.internal.APContext.logError;
import static io.avaje.recordbuilder.internal.APContext.typeElement;
import static io.avaje.recordbuilder.internal.Templates.methodAdd;
import static io.avaje.recordbuilder.internal.Templates.methodGetter;
import static io.avaje.recordbuilder.internal.Templates.methodPut;
import static io.avaje.recordbuilder.internal.Templates.methodSetter;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateUtils;

@GenerateUtils
@GenerateAPContext
@SupportedAnnotationTypes({RecordBuilderPrism.PRISM_TYPE, ImportPrism.PRISM_TYPE})
public final class RecordProcessor extends AbstractProcessor {

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    APContext.init(env);
  }

  @Override
  public boolean process(Set<? extends TypeElement> tes, RoundEnvironment roundEnv) {
    final var globalTypeInitializers =
        roundEnv.getElementsAnnotatedWith(typeElement(GlobalPrism.PRISM_TYPE)).stream()
            .map(GlobalPrism::getInstanceOn)
            .collect(toMap(s -> s.type().toString(), GlobalPrism::value));

    InitMap.putAll(globalTypeInitializers);
    APContext.setProjectModuleElement(tes, roundEnv);
    for (final TypeElement type : ElementFilter.typesIn(roundEnv.getElementsAnnotatedWith(typeElement(RecordBuilderPrism.PRISM_TYPE)))) {
      if (type.getKind() != ElementKind.RECORD) {
        logError(type, "Builders can only be generated for record classes");
        continue;
      }
      readElement(type);
    }

    roundEnv.getElementsAnnotatedWith(typeElement(ImportPrism.PRISM_TYPE)).stream()
        .map(ImportPrism::getInstanceOn)
        .map(ImportPrism::value)
        .flatMap(List::stream)
        .map(APContext::asTypeElement)
        .forEach(this::readElement);

    if (roundEnv.processingOver()) {
      try (var reader = getModuleInfoReader()) {
        ModuleReader.read(reader);
      } catch (IOException e) {
        // Can't read module, it's whatever
      }
    }
    return false;
  }

  private void readElement(TypeElement type) {
    readElement(type, false);
  }

  private void readElement(TypeElement type, boolean isImported) {
    final var components = type.getRecordComponents();
    final var packageElement = elements().getPackageOf(type);
    var unnamed = Utils.isInUnnamedPackage(isImported, packageElement);
    final var packageName =
        unnamed
            ? ""
            : packageElement.getQualifiedName().toString() + (isImported ? ".builder" : "");
    final var shortName = type.getSimpleName().toString();

    try (var writer =
        new Append(
            createSourceFile((unnamed ? "" : packageName + ".") + shortName + "Builder")
                .openWriter())) {

      var typeParams =
          type.getTypeParameters().stream()
              .map(Object::toString)
              .collect(joining(", "))
              .transform(s -> s.isEmpty() ? s : "<" + s + ">");
      writer.append(ClassBodyBuilder.createClassStart(type, typeParams, isImported, packageName));
      final var writeGetters = RecordBuilderPrism.getInstanceOn(type).getters();
      methods(new WriteContext(writer, typeParams, shortName, components, writeGetters));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private record WriteContext(
    Append writer,
    String typeParams,
    String shortName,
    List<? extends RecordComponentElement> components,
    Boolean writeGetters
  ) {
    boolean getters() {
      return Boolean.TRUE.equals(writeGetters);
    }

    void append(String content) {
      writer.append(content);
    }
  }

  private void methods(WriteContext ctx) {
    for (final var element : ctx.components()) {
      final var type = UType.parse(element.asType());
      ctx.append(methodSetter(element.getSimpleName(), type.shortType(), ctx.shortName(), ctx.typeParams()));
      if (ctx.getters()) {
        writeGetter(ctx, element, type);
      }
      if (APContext.isAssignable(type.mainType(), "java.util.Collection")) {
        writeAdd(ctx, element, type);
      }
      if (APContext.isAssignable(type.mainType(), "java.util.Map")) {
        writePut(ctx, element, type);
      }
    }
    ctx.append("}");
  }

  private static void writePut(WriteContext ctx, RecordComponentElement element, UType type) {
    String param0ShortType = type.param0().shortType();
    String param1ShortType = type.param1().shortType();
    Name simpleName = element.getSimpleName();
    ctx.append(
      methodPut(
        simpleName.toString(),
        type.shortType().transform(ProcessorUtils::trimAnnotations),
        ctx.shortName(),
        param0ShortType,
        param1ShortType,
        ctx.typeParams()));
  }

  private static void writeAdd(WriteContext ctx, RecordComponentElement element, UType type) {
    String param0ShortType = type.param0().shortType();
    Name simpleName = element.getSimpleName();
    ctx.append(
      methodAdd(
        simpleName.toString(), type.shortType(), ctx.shortName(), param0ShortType, ctx.typeParams()));
  }

  private static void writeGetter(WriteContext ctx, RecordComponentElement element, UType type) {
    ctx.append(
      methodGetter(
        element.getSimpleName(),
        type.shortType().transform(ProcessorUtils::trimAnnotations),
        ctx.shortName()));
  }
}
