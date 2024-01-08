package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.Templates.classTemplate;
import static java.util.stream.Collectors.joining;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

public class ClassBodyBuilder {

  private ClassBodyBuilder() {}

  static String createClassStart(
      BuilderPrism prism,
      TypeElement type,
      String typeParams,
      boolean isImported,
      String packageName) {

    final var components = type.getRecordComponents();
    final var shortName = type.getSimpleName().toString();
    if (type.getEnclosingElement() instanceof TypeElement) {
      isImported = true;
    }
    var utype = UType.parse(type.asType());
    var fulltypeParams =
        utype.componentTypes().stream()
            .map(
                t ->
                    t.shortType()
                        + Optional.ofNullable(t.param0())
                            .map(UType::shortType)
                            .map(s -> " extends " + s)
                            .orElse(""))
            .collect(joining(", "))
            .transform(s -> s.replace(" extends java.lang.Object", ""))
            .transform(s -> s.isEmpty() ? s : "<" + s + ">");

    final RecordModel rm = new RecordModel(type, isImported, components, utype);
    rm.initialImports();
    rm.nullableAnnotation(prism.nullableAnnotation().toString());
    var implementsStr =
        prism.builderInterfaces().stream()
            .map(TypeMirror::toString)
            .peek(rm::addImport)
            .map(ProcessorUtils::shortType)
            .collect(joining(", "))
            .transform(s -> s.isEmpty() ? s : "implements " + s);
    final String fieldString = rm.fields();
    final var imports = rm.importsFormat();
    final var numberOfComponents = components.size();

    final String constructorParams = constructorParams(components, numberOfComponents > 5);
    final String constructorBody = constructorBody(components);
    final String builderFrom =
        builderFrom(components).transform(s -> numberOfComponents > 5 ? "\n        " + s : s);
    final String build =
        build(components, prism).transform(s -> numberOfComponents > 6 ? "\n        " + s : s);
    return classTemplate(
        packageName,
        imports,
        shortName,
        implementsStr,
        fieldString,
        constructorParams,
        constructorBody,
        builderFrom,
        build,
        fulltypeParams,
        typeParams);
  }

  private static String constructorParams(
      List<? extends RecordComponentElement> components, boolean verticalArgs) {

    return components.stream()
        .map(r -> UType.parse(r.asType()).shortType() + " " + r.getSimpleName())
        .collect(joining(verticalArgs ? ",\n      " : ", "))
        .transform(s -> verticalArgs ? "\n      " + s : s);
  }

  private static String constructorBody(List<? extends RecordComponentElement> components) {
    return components.stream()
        .map(RecordComponentElement::getSimpleName)
        .map(s -> MessageFormat.format("this.{0} = {0};", s))
        .collect(joining("\n    "));
  }

  private static String builderFrom(List<? extends RecordComponentElement> components) {
    return components.stream()
        .map(RecordComponentElement::getSimpleName)
        .map("from.%s()"::formatted)
        .collect(joining(", "));
  }

  private static String build(
      List<? extends RecordComponentElement> components, BuilderPrism prism) {

    return components.stream()
        .map(
            element ->
                (prism.enforceNullSafety() && !Utils.isNullable(element))
                        || Utils.isNonNullable(element)
                    ? "requireNonNull(%s)".formatted(element.getSimpleName())
                    : element.getSimpleName())
        .collect(joining(", "));
  }
}
