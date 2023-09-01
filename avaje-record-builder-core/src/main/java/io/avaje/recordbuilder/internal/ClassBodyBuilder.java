package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.elements;
import static io.avaje.recordbuilder.internal.Templates.classTemplate;
import static java.util.stream.Collectors.joining;

import java.text.MessageFormat;
import java.util.List;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;

// TODO better name?
public class ClassBodyBuilder {

  static String createClassStart(TypeElement type, boolean isImported) {

    final var components = type.getRecordComponents();
    final var packageName =
        elements().getPackageOf(type).getQualifiedName().toString()
            + (isImported ? ".builder" : "");
    final var shortName = type.getSimpleName().toString();
    if (type.getEnclosingElement() instanceof TypeElement) {
      isImported = true;
    }
    final RecordModel rm = new RecordModel(type, isImported, components);
    rm.initialImports();
    final String fieldString = rm.fields();
    final var imports = rm.importsFormat();
    final var numberOfComponents = components.size();

    // String fieldString = fields(components);
    final String constructorParams = constructorParams(components, numberOfComponents > 5);
    final String constructorBody = constructorBody(components);
    final String builderFrom =
        builderFrom(components).transform(s -> numberOfComponents > 5 ? "\n        " + s : s);
    final String build =
        build(components).transform(s -> numberOfComponents > 6 ? "\n        " + s : s);
    return classTemplate(
        packageName,
        imports,
        shortName,
        fieldString,
        constructorParams,
        constructorBody,
        builderFrom,
        build);
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

  private static String build(List<? extends RecordComponentElement> components) {
    return components.stream().map(RecordComponentElement::getSimpleName).collect(joining(", "));
  }
}
