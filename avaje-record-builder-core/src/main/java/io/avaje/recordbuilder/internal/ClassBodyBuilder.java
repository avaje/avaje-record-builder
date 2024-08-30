package io.avaje.recordbuilder.internal;

import static java.util.stream.Collectors.joining;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;

import io.avaje.recordbuilder.internal.Templates.ClassTemplate;

public class ClassBodyBuilder {

  private ClassBodyBuilder() {}

  static String createClassStart(
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
    final String fieldString = rm.fields();
    final var imports = rm.importsFormat();
    final var numberOfComponents = components.size();

    final String constructorParams = constructorParams(components, numberOfComponents > 5);
    final String constructorBody = constructorBody(components);
    final String builderFrom =
        builderFrom(components).transform(s -> numberOfComponents > 5 ? "\n        " + s : s);
    final String build =
        build(components).transform(s -> numberOfComponents > 6 ? "\n        " + s : s);
    return ClassTemplate.classTemplate(
        packageName,
        imports,
        shortName,
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
      List<? extends RecordComponentElement> components) {

    return components.stream()
        .map(
            element -> {
              final var simpleName = element.getSimpleName();
              return !Utils.isNullableType(UType.parse(element.asType()).mainType())
                      && Utils.isNonNullable(element)
                  ? "requireNonNull(%s, \"%s\")".formatted(simpleName, simpleName)
                  : simpleName;
            })
        .collect(joining(", "));
  }
}
