package io.avaje.recordbuilder.internal;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;

public class RecordModel {

  private final TypeElement type;
  private final boolean isImported;
  private final List<? extends RecordComponentElement> components;

  private final Set<String> importTypes = new TreeSet<>();

  public RecordModel(
      TypeElement type, boolean isImported, List<? extends RecordComponentElement> components) {
    this.type = type;
    this.isImported = isImported;
    this.components = components;
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

  String fields(Map<String, String> defaultsMap) {
    final var builder = new StringBuilder();
    for (final var element : components) {
      final var type = UType.parse(element.asType());

      String defaultVal = "";
      final DefaultInitPrism initPrism = DefaultInitPrism.getInstanceOn(element);
      if (initPrism != null) {
        defaultVal = " = " + initPrism.value();
      } else {
        final String dt = defaultsMap.get(type.mainType());
        if (dt != null) {
          importTypes.add(dt);

          defaultVal = " = new " + ProcessorUtils.shortType(dt) + "<>()";
        }
      }

      builder.append(
          "  private %s %s%s;  // -- %s\n"
              .formatted(type.shortType(), element.getSimpleName(), defaultVal, type.mainType()));
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
