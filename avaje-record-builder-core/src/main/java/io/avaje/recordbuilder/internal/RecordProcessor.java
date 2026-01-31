package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.createSourceFile;
import static io.avaje.recordbuilder.internal.APContext.elements;
import static io.avaje.recordbuilder.internal.APContext.getModuleInfoReader;
import static io.avaje.recordbuilder.internal.APContext.logError;
import static io.avaje.recordbuilder.internal.APContext.typeElement;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateUtils;
import io.avaje.recordbuilder.internal.Templates.MethodAdd;
import io.avaje.recordbuilder.internal.Templates.MethodGetter;
import io.avaje.recordbuilder.internal.Templates.MethodPut;
import io.avaje.recordbuilder.internal.Templates.MethodSetter;

@GenerateUtils
@GenerateAPContext
@SupportedAnnotationTypes({
  RecordBuilderPrism.PRISM_TYPE,
  ImportPrism.PRISM_TYPE,
  GlobalPrism.PRISM_TYPE
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
  }

  @Override
  public boolean process(Set<? extends TypeElement> tes, RoundEnvironment roundEnv) {
    APContext.setProjectModuleElement(tes, roundEnv);
    final var globalTypeInitializers =
        roundEnv.getElementsAnnotatedWith(typeElement(GlobalPrism.PRISM_TYPE)).stream()
            .map(GlobalPrism::getInstanceOn)
            .collect(toMap(s -> s.type().toString(), GlobalPrism::value));

    InitMap.putAll(globalTypeInitializers);

    var elements = roundEnv.getElementsAnnotatedWith(typeElement(RecordBuilderPrism.PRISM_TYPE));

    var records = new HashSet<>(ElementFilter.typesIn(elements));
    allPackages(elements)
        .forEach(e -> findRecordsInPackage(e, records));

    for (final TypeElement type : records) {
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
      APContext.clear();
    }
    return false;
  }

  private static Stream<? extends Element> allPackages(Set<? extends Element> elements) {
    return Stream.concat(
      modulePackages(elements),
      ElementFilter.packagesIn(elements).stream());
  }

  private static Stream<? extends Element> modulePackages(Set<? extends Element> elements) {
    return ElementFilter.modulesIn(elements).stream()
      .map(Element::getEnclosedElements)
      .flatMap(List::stream);
  }

  private void findRecordsInPackage(Element pkg, Set<TypeElement> records) {
    for (var enclosedElement : ElementFilter.typesIn(pkg.getEnclosedElements())) {
      if (enclosedElement.getKind() == ElementKind.RECORD) {
        records.add(enclosedElement);
      }
      findNestedRecords(enclosedElement, records);
    }
  }

  private void findNestedRecords(TypeElement type, Set<TypeElement> types) {
    for (var enclosedElement : ElementFilter.typesIn(type.getEnclosedElements())) {
      if (enclosedElement.getKind() == ElementKind.RECORD) {
        types.add(enclosedElement);
      }
      findNestedRecords(enclosedElement, types);
    }
  }

  private void readElement(TypeElement type) {
    readElement(type, findRecordBuilderPrism(type));
  }

  private static RecordBuilderPrism findRecordBuilderPrism(Element element) {
    var prism = RecordBuilderPrism.getInstanceOn(element);
    return prism != null ? prism : findRecordBuilderPrism(element.getEnclosingElement());
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
    var builderName = builderName(type);
    try (var writer =
        new Append(
            createSourceFile((unnamed ? "" : packageName + ".") + builderName).openWriter())) {

      var typeParams =
          type.getTypeParameters().stream()
              .map(Object::toString)
              .collect(joining(", "))
              .transform(s -> s.isEmpty() ? s : "<" + s + ">");
      writer.append(ClassBodyBuilder.createClassStart(type, typeParams, isImported, packageName, builderName));

      methods(writer, typeParams, builderName, components, prism);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String builderName(TypeElement type) {
    var name = ProcessorUtils.shortType(UType.parse(type.asType()).mainType());
    int pos = name.lastIndexOf('.');
    if (pos > 0 && isNotNestedRecord(type.getEnclosingElement())) {
      return name.substring(pos + 1) + "Builder";
    }
    return name.replace(".", "$") + "Builder";
  }

  private static boolean isNotNestedRecord(Element enclosingElement) {
    ElementKind kind = enclosingElement.getKind();
    return kind != ElementKind.RECORD;
  }

  private void methods(
      Append writer,
      String typeParams,
      String builderName,
      List<? extends RecordComponentElement> components,
      BuilderPrism prism) {
    boolean getters = prism.getters();

    for (final var element : components) {
      final var type = UType.parse(element.asType());
      writer.append(
          MethodSetter.methodSetter(
              element.getSimpleName(), type.shortType(), builderName, typeParams));
      if (getters) {
        writer.append(MethodGetter.methodGetter(element.getSimpleName(), type, builderName));
      }

      if (APContext.isAssignable(type.mainType(), "java.util.Collection")) {
        String param0ShortType = type.param0().shortType();
        Name simpleName = element.getSimpleName();
        writer.append(
            MethodAdd.methodAdd(simpleName.toString(), builderName, param0ShortType, typeParams));
      }

      if (APContext.isAssignable(type.mainType(), "java.util.Map")) {
        String param0ShortType = type.param0().shortType();
        String param1ShortType = type.param1().shortType();
        Name simpleName = element.getSimpleName();
        writer.append(
            MethodPut.methodPut(
                simpleName.toString(), builderName, param0ShortType, param1ShortType, typeParams));
      }
    }
    writer.append("}");
  }
}
