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
      String packageName,
      String builderName,
      List<? extends RecordComponentElement> stagedComponents) {

    if (type.getEnclosingElement() instanceof TypeElement) {
      isImported = true;
    }

    var utype = UType.parse(type.asType());
    final var components = type.getRecordComponents();

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

    final var stageNames = stagedComponents.stream().map(Utils::stageName).toList();
    final String stages = stages(stagedComponents, builderName, fulltypeParams, typeParams);

    final var shortName = type.getSimpleName().toString();
    return ClassTemplate.classTemplate(
        packageName,
        imports,
        builderName,
        shortName,
        fieldString,
        constructorParams,
        constructorBody,
        builderFrom,
        build,
        fulltypeParams,
        typeParams,
        stageNames,
        stages);
  }

  private static String stages(
      List<? extends RecordComponentElement> stagedComponents,
      String builderName,
      String fullTypeParams,
      String typeParams) {

    if (stagedComponents.isEmpty()) {
      return "";
    }
    var stages = new StringBuilder();
    var impl = new StringBuilder();
    for (int i = 0; i < stagedComponents.size(); i++) {
      var element = stagedComponents.get(i);
      var next =
          i + 1 < stagedComponents.size()
              ? Utils.stageName(stagedComponents.get(i + 1))
              : builderName;
      var last = i + 1 == stagedComponents.size();
      var stageName = Utils.stageName(element);
      var collectionMethods = new StringBuilder();
      var collectionImpl = new StringBuilder();
      collectionStageMethods(
          element,
          stageName + typeParams,
          next + typeParams,
          last ? "builder" : "this",
          collectionMethods,
          collectionImpl);
      stages.append(
          new Templates.StageInterface(
                  stageName,
                  element.getSimpleName().toString(),
                  UType.parse(element.asType()).shortType(),
                  next,
                  fullTypeParams,
                  typeParams,
                  collectionMethods.toString())
              .render());
      impl.append(collectionImpl);
      impl.append(
          MessageFormat.format(
              """

                  public {0}{1} {2}({3} {2}) '{'
                    builder.{2}({2});
                    return {4};
                  '}'
              """,
              next,
              typeParams,
              element.getSimpleName(),
              UType.parse(element.asType()).shortType(),
              last ? "builder" : "this"));
    }
    var implementing =
        stagedComponents.stream()
            .map(e -> Utils.stageName(e) + typeParams)
            .collect(joining(", "));
    stages.append(
        MessageFormat.format(
            """

              private static final class Stages{0} implements {1} '{'

                private final {2}{3} builder = new {2}{3}();
            {4}  '}'
            """,
            fullTypeParams,
            implementing,
            builderName,
            typeParams,
            impl));
    return stages.toString();
  }

  /**
   * For a Collection or Map stage, render the add/put method that stays on the stage and the
   * {@code end<Name>()} method that moves on to the next stage.
   */
  private static void collectionStageMethods(
      RecordComponentElement element,
      String stageType,
      String nextType,
      String nextValue,
      StringBuilder methods,
      StringBuilder impl) {

    if (!Utils.isCollection(element)) {
      return;
    }
    var type = UType.parse(element.asType());
    var name = element.getSimpleName().toString();
    var upperCamel = Character.toUpperCase(name.charAt(0)) + name.substring(1);
    String method;
    String params;
    String args;
    if (APContext.isAssignable(type.mainType(), "java.util.Map")) {
      method = "put" + upperCamel;
      params = type.param0().shortType() + " key, " + type.param1().shortType() + " value";
      args = "key, value";
    } else {
      method = "add" + upperCamel;
      params = type.param0().shortType() + " element";
      args = "element";
    }
    methods.append(
        MessageFormat.format(
            """


                {0} {1}({2});

                /** Finish '{'@code {3} '}' and move to the next stage. */
                {4} end{5}();\
            """,
            stageType,
            method,
            params,
            name,
            nextType,
            upperCamel));
    impl.append(
        MessageFormat.format(
            """

                public {0} {1}({2}) '{'
                  builder.{1}({3});
                  return this;
                '}'

                public {4} end{5}() '{'
                  return {6};
                '}'
            """,
            stageType,
            method,
            params,
            args,
            nextType,
            upperCamel,
            nextValue));
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
