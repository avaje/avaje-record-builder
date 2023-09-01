package io.avaje.recordbuilder.internal;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

final class RecordModel {

  private final TypeElement type;
  private final boolean isImported;
  private final List<? extends RecordComponentElement> components;

  private final Set<String> importTypes = new TreeSet<>();

  // Create a Pattern object
  Pattern JAVA_UTIL = Pattern.compile("new\\s+(java\\.util\\.[A-Za-z]+)");
  Pattern OPTIONAL = Pattern.compile("(java\\.util\\.[A-Za-z]+)");

  RecordModel(
      TypeElement type, boolean isImported, List<? extends RecordComponentElement> components) {
    this.type = type;
    this.isImported = isImported;
    this.components = components;
    importTypes.add("io.avaje.recordbuilder.Generated");
  }

  void initialImports() {
    final Set<String> types =
        components.stream()
            .map(RecordComponentElement::asType)
            .filter(not(PrimitiveType.class::isInstance))
            .map(TypeMirror::toString)
            .map(ProcessorUtils::trimAnnotations)
            .flatMap(s -> Arrays.stream(s.split("[<|>|,]")))
            .map(Utils::extractTypeWithNest)
            .distinct()
            .filter(not(String::isBlank))
            .filter(s -> !s.startsWith("java.lang"))
            .collect(Collectors.toSet());

    importTypes.addAll(types);
  }

  String fields() {
    final var builder = new StringBuilder();
    for (final var element : components) {
      final var uType = UType.parse(element.asType());

      String defaultVal = "";
      final DefaultInitPrism initPrism = DefaultInitPrism.getInstanceOn(element);
      if (initPrism != null) {
        defaultVal = " = " + initPrism.value();
      } else {
        final String dt = InitMap.get(uType.mainType());
        if (dt != null) {
          var javaUtil = dt.startsWith("new java.util");
          var optional = dt.startsWith("java.util.Optional");
          if (javaUtil || optional) {
            if (optional) {
              var matcher = OPTIONAL.matcher(dt);
              matcher.find();
              importTypes.add(matcher.group(0));
            } else {
              var matcher = JAVA_UTIL.matcher(dt);
              matcher.find();
              importTypes.add(matcher.group(1));
            }
            defaultVal = " = " + dt.replace("java.util.", "");
          } else {
            defaultVal = " = " + dt;
          }
        }
      }

      builder.append(
          "  private %s %s%s;\n".formatted(uType.shortType(), element.getSimpleName(), defaultVal));
    }

    return builder.toString();
  }

  String importsFormat() {
    return importTypes.stream()
        .map(s -> "import " + s + ";")
        .collect(joining("\n"))
        .transform(s -> s + (isImported ? "\nimport " + type.getQualifiedName() + ";" : ""))
        .lines()
        .collect(joining("\n"));
  }
}
