package io.avaje.recordbuilder.internal;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.avaje.recordbuilder.internal.APContext.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

// TODO break up this God class
@GenerateUtils
@GenerateAPContext
@SupportedAnnotationTypes({RecordBuilderPrism.PRISM_TYPE, ImportPrism.PRISM_TYPE})
public class RecordProcessor extends AbstractProcessor {

  static Map<String, String> defaultsMap = new HashMap<>();

  static {
    // TODO add the rest of the collections
    final var util = "java.util.";
    defaultsMap.put(util + "Collection", util + "ArrayList");
    defaultsMap.put(util + "SequencedCollection", util + "ArrayList");
    // list
    defaultsMap.put(util + "List", util + "ArrayList");
    defaultsMap.put(util + "ArrayList", util + "ArrayList");
    defaultsMap.put(util + "LinkedList", util + "LinkedList");
    // set
    defaultsMap.put(util + "Set", util + "HashSet");
    defaultsMap.put(util + "SequencedSet", util + "LinkedHashSet");
    defaultsMap.put(util + "HashSet", util + "HashSet");
    defaultsMap.put(util + "TreeSet", util + "TreeSet");
    defaultsMap.put(util + "SortedSet", util + "TreeSet");
    defaultsMap.put(util + "NavigableSet", util + "TreeSet");
    defaultsMap.put(util + "LinkedHashSet", util + "LinkedHashSet");
    // map
    defaultsMap.put(util + "Map", util + "HashMap");
    defaultsMap.put(util + "SequencedMap", util + "LinkedHashMap");
    defaultsMap.put(util + "HashMap", util + "HashMap");
    defaultsMap.put(util + "LinkedHashMap", util + "LinkedHashMap");
    defaultsMap.put(util + "TreeMap", util + "TreeMap");
    defaultsMap.put(util + "SortedMap", util + "TreeMap");
    defaultsMap.put(util + "NavigableMap", util + "TreeMap");

    // queue

    // deque
  }

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

    defaultsMap.putAll(globalTypeInitializers);
    APContext.setProjectModuleElement(tes, roundEnv);
    for (final TypeElement type :
      ElementFilter.typesIn(
        roundEnv.getElementsAnnotatedWith(typeElement(RecordBuilderPrism.PRISM_TYPE)))) {
      if (type.getRecordComponents().isEmpty()) {
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
    final var packageName =
      elements().getPackageOf(type).getQualifiedName().toString()
        + (isImported ? ".builder" : "");
    final var shortName = type.getSimpleName().toString();
    if (type.getEnclosingElement() instanceof TypeElement) {
      isImported = true;
    }
    final RecordModel rm = new RecordModel(type, isImported, components);
    rm.initialImports();
    final String fieldString = rm.fields(defaultsMap);
    final var imports = rm.importsFormat();
    final var numberOfComponents = components.size();

    // String fieldString = fields(components);
    final String constructorParams = constructorParams(components, numberOfComponents > 5);
    final String constructorBody = constructorBody(components);
    final String builderFrom =
      builderFrom(components).transform(s -> numberOfComponents > 5 ? "\n        " + s : s);
    final String build =
      build(components).transform(s -> numberOfComponents > 6 ? "\n        " + s : s);

    try (var writer =
           new Append(createSourceFile(packageName + "." + shortName + "Builder").openWriter())) {
      final var temp =
        template(
          packageName,
          imports,
          shortName,
          fieldString,
          constructorParams,
          constructorBody,
          builderFrom,
          build);
      writer.append(temp);
      final var writeGetters = RecordBuilderPrism.getInstanceOn(type).getters();
      methods(writer, shortName, components, writeGetters);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static String fields(List<? extends RecordComponentElement> components) {
    final var builder = new StringBuilder();
    for (final var element : components) {
      final var type = UType.parse(element.asType());

      final var defaultVal =
        DefaultInitPrism.getOptionalOn(element)
          .map(DefaultInitPrism::value)
          .orElseGet(() -> defaultsMap.getOrDefault(type.mainType(), ""))
          .transform(s -> s.isBlank() ? s : " = " + s);

      builder.append(
        "  private %s %s%s;\n".formatted(type.shortType(), element.getSimpleName(), defaultVal));
    }

    return builder.toString();
  }

  static String constructorParams(
    List<? extends RecordComponentElement> components, boolean verticalArgs) {

    return components.stream()
      .map(r -> UType.parse(r.asType()).shortType() + " " + r.getSimpleName())
      .collect(joining(verticalArgs ? ",\n      " : ", "))
      .transform(s -> verticalArgs ? "\n      " + s : s);
  }

  static String constructorBody(List<? extends RecordComponentElement> components) {

    return components.stream()
      .map(RecordComponentElement::getSimpleName)
      .map(s -> MessageFormat.format("this.{0} = {0};", s))
      .collect(joining("\n    "));
  }

  static String builderFrom(List<? extends RecordComponentElement> components) {

    return components.stream()
      .map(RecordComponentElement::getSimpleName)
      .map("from.%s()"::formatted)
      .collect(joining(", "));
  }

  static String build(List<? extends RecordComponentElement> components) {

    return components.stream().map(RecordComponentElement::getSimpleName).collect(joining(", "));
  }

  private void methods(
    Append writer,
    String shortName,
    List<? extends RecordComponentElement> components,
    Boolean writeGetters) {

    boolean getters = Boolean.TRUE.equals(writeGetters);

    for (final var element : components) {
      final var type = UType.parse(element.asType());
      writer.append(methodSetter(element.getSimpleName(), type.shortType(), shortName));
      if (getters) {
        writer.append(methodGetter(element.getSimpleName(), type.shortType(), shortName));
      }
      TypeElement typeElement = asTypeElement(element.asType());
      if (APContext.isAssignable(typeElement, "java.util.Collection")) {
        String param0 = type.param0();
        String param0ShortType = UType.parse(param0).shortType();
        Name simpleName = element.getSimpleName();
        writer.append(methodAdd(simpleName.toString(), type.shortType(), shortName, param0ShortType));
      }
    }
    writer.append("}");
  }

  String methodSetter(CharSequence componentName, String type, String shortName) {
    return MessageFormat.format(
      """

          /** Set a new value for '{'@code {0}'}'. */
          public {2}Builder {0}({1} {0}) '{'
              this.{0} = {0};
              return this;
          '}'
        """,
      componentName, type, shortName.replace(".", "$"));
  }

  String methodGetter(CharSequence componentName, String type, String shortName) {
    return MessageFormat.format(
      """

          /** Return the current value for '{'@code {0}'}'. */
          public {1} {0}() '{'
              return {0};
          '}'
        """,
      componentName, type, shortName.replace(".", "$"));
  }

  String methodAdd(String componentName, String type, String shortName, String param0) {
    String upperCamal = Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);
    return MessageFormat.format(
      """

          /** Add to the '{'@code {0}'}'. */
          public {2}Builder add{3}({4} element) '{'
              this.{0}.add(element);
              return this;
          '}'
        """,
      componentName, type, shortName.replace(".", "$"), upperCamal, param0);
  }

  String template(
    String packageName,
    String imports,
    String shortName,
    String fields,
    String constructor,
    String constructorBody,
    String builderFrom,
    String build) {

    return MessageFormat.format(
      """
        package {0};

        {1}

        /**  Builder class for '{'@link {2}'}' */
        public class {2}Builder '{'
        {3}
          private {2}Builder() '{'
          '}'

          private {2}Builder({4}) '{'
            {5}
          '}'

          /**
           * Return a new builder with all fields set to default Java values
           */
          public static {2}Builder builder() '{'
              return new {2}Builder();
          '}'

          /**
           * Return a new builder with all fields set to the values taken from the given record instance
           */
          public static {2}Builder builder({2} from) '{'
              return new {2}Builder({6});
          '}'

          /**
           * Return a new {2} instance with all fields set to the current values in this builder
           */
          public {2} build() '{'
              return new {2}({7});
          '}'
        """,
      packageName, imports, shortName, fields, constructor, constructorBody, builderFrom, build);
  }
}
