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
@SupportedAnnotationTypes({
  RecordBuilderPrism.PRISM_TYPE,
  ImportPrism.PRISM_TYPE,
  GlobalConfigPrism.PRISM_TYPE
})
public final class RecordProcessor extends AbstractProcessor {

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public synchronized void init(ProcessingEnvironment env) {
    super.init(env);
    APContext.init(env);
    GlobalSettings.init();
  }

  @Override
  public boolean process(Set<? extends TypeElement> tes, RoundEnvironment roundEnv) {

    roundEnv.getElementsAnnotatedWith(typeElement(GlobalConfigPrism.PRISM_TYPE)).stream()
        .map(GlobalConfigPrism::getInstanceOn)
        .findFirst()
        .ifPresent(GlobalSettings::configure);

    final var globalTypeInitializers =
        roundEnv.getElementsAnnotatedWith(typeElement(GlobalPrism.PRISM_TYPE)).stream()
            .map(GlobalPrism::getInstanceOn)
            .collect(toMap(s -> s.type().toString(), GlobalPrism::value));

    InitMap.putAll(globalTypeInitializers);
    APContext.setProjectModuleElement(tes, roundEnv);
    for (final TypeElement type :
        ElementFilter.typesIn(
            roundEnv.getElementsAnnotatedWith(typeElement(RecordBuilderPrism.PRISM_TYPE)))) {
      if (type.getKind() != ElementKind.RECORD) {
        logError(type, "Builders can only be generated for record classes");
        continue;
      }

      readElement(type);
    }

    roundEnv.getElementsAnnotatedWith(typeElement(ImportPrism.PRISM_TYPE)).stream()
        .map(ImportPrism::getInstanceOn)
        .forEach(
            prism ->
                prism.value().stream()
                    .map(APContext::asTypeElement)
                    .forEach(t -> readElement(t, prism)));

    if (roundEnv.processingOver()) {
      try (var reader = getModuleInfoReader()) {

        ModuleReader.read(reader);
      } catch (IOException e) {
        // Can't read module, it's whatever
      }
      GlobalSettings.clear();
      APContext.clear();
    }
    return false;
  }

  private void readElement(TypeElement type) {
    readElement(type, RecordBuilderPrism.getInstanceOn(type));
  }

  private void readElement(TypeElement type, BuilderPrism prism) {

    final var components = type.getRecordComponents();
    final var packageElement = elements().getPackageOf(type);
    boolean isImported = prism.imported();
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
      writer.append(
          ClassBodyBuilder.createClassStart(prism, type, typeParams, isImported, packageName));

      methods(writer, typeParams, shortName, components, prism);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void methods(
      Append writer,
      String typeParams,
      String shortName,
      List<? extends RecordComponentElement> components,
      BuilderPrism prism) {
    boolean getters = GlobalSettings.getters() || prism.getters();

    for (final var element : components) {
      final var type = UType.parse(element.asType());
      writer.append(methodSetter(element.getSimpleName(), type.shortType(), shortName, typeParams));
      if (getters) {
        writer.append(
            methodGetter(
                element.getSimpleName(),
                type,
                shortName));
      }

      if (APContext.isAssignable(type.mainType(), "java.util.Collection")) {

        String param0ShortType = type.param0().shortType();
        Name simpleName = element.getSimpleName();
        writer.append(
            methodAdd(
                simpleName.toString(), type.shortType(), shortName, param0ShortType, typeParams));
      }

      if (APContext.isAssignable(type.mainType(), "java.util.Map")) {

        String param0ShortType = type.param0().shortType();
        String param1ShortType = type.param1().shortType();
        Name simpleName = element.getSimpleName();
        writer.append(
            methodPut(
                simpleName.toString(),
                type.shortType().transform(ProcessorUtils::trimAnnotations),
                shortName,
                param0ShortType,
                param1ShortType,
                typeParams));
      }
    }
    writer.append("}");
  }
}
