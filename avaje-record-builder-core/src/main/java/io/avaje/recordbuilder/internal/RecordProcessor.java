package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.createSourceFile;
import static io.avaje.recordbuilder.internal.APContext.elements;
import static io.avaje.recordbuilder.internal.APContext.logError;
import static io.avaje.recordbuilder.internal.APContext.typeElement;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import io.avaje.prism.GenerateAPContext;
import io.avaje.prism.GenerateUtils;

// TODO break up this God class
@GenerateUtils
@GenerateAPContext
@SupportedAnnotationTypes({RecordBuilderPrism.PRISM_TYPE, ImportPrism.PRISM_TYPE})
public class RecordProcessor extends AbstractProcessor {

  static Map<String, String> defaultsMap = new HashMap<>();

  static {
    var util = "java.util.%s";
    var init = "new %s()";
    var initDiamond = "new %s<>()";
    defaultsMap.put(util.formatted("List"), initDiamond.formatted("ArrayList"));
    defaultsMap.put(util.formatted("ArrayList"), initDiamond.formatted("ArrayList"));
    defaultsMap.put(util.formatted("LinkedList"), initDiamond.formatted("LinkedList"));
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

    var globalTypeInitializers =
        roundEnv.getElementsAnnotatedWith(typeElement(GlobalPrism.PRISM_TYPE)).stream()
            .map(GlobalPrism::getInstanceOn)
            .collect(toMap(s -> s.type().toString(), GlobalPrism::value));

    defaultsMap.putAll(globalTypeInitializers);
    APContext.setProjectModuleElement(tes, roundEnv);
    for (TypeElement type :
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

    return false;
  }

  private void readElement(TypeElement type) {
    readElement(type, false);
  }

  private void readElement(TypeElement type, boolean isImported) {

    var components = type.getRecordComponents();
    var packageName =
        elements().getPackageOf(type).getQualifiedName().toString()
            + (isImported ? ".builder" : "");
    var shortName = type.getSimpleName().toString();
    if (type.getEnclosingElement() instanceof TypeElement) {
      isImported = true;
    }
    var imports = imports(type, isImported, components);
    var numberOfComponents = components.size();

    String fieldString = fields(components);
    String constructorParams = constructorParams(components, numberOfComponents > 5);
    String constructorBody = constructorBody(components);
    String builderFrom =
        builderFrom(components).transform(s -> numberOfComponents > 5 ? "\n        " + s : s);
    String build = build(components).transform(s -> numberOfComponents > 6 ? "\n        " + s : s);

    try (var writer =
        new Append(createSourceFile(packageName + "." + shortName + "Builder").openWriter())) {
      var temp =
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
      methods(writer, shortName, components);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  static String imports(
      TypeElement type, boolean isImpoorted, List<? extends RecordComponentElement> components) {

    return components.stream()
        .map(RecordComponentElement::asType)
        .filter(not(PrimitiveType.class::isInstance))
        .map(TypeMirror::toString)
        .map(ProcessorUtils::trimAnnotations)
        .flatMap(s -> Arrays.stream(s.split("[<|>|,]")))
        .map(Utils::extractTypeWithNest)
        .distinct()
        .filter(not(String::isBlank))
        .filter(s -> !s.startsWith("java.lang"))
        .map(s -> "import " + s + ";")
        .collect(joining("\n"))
        .transform(s -> s + (isImpoorted ? "\nimport " + type.getQualifiedName() + ";" : ""))
        .lines()
        .distinct()
        .collect(joining("\n"));
  }

  static String fields(List<? extends RecordComponentElement> components) {
    var builder = new StringBuilder();
    for (var element : components) {
      var type = UType.parse(element.asType());

      var defaultVal =
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
      Append writer, String shortName, List<? extends RecordComponentElement> components) {
    for (var element : components) {
      var type = UType.parse(element.asType());

      writer.append(methodTemplate(element.getSimpleName(), type.shortType(), shortName));
    }
    writer.append("}");
  }

  String methodTemplate(CharSequence componentName, String type, String shortName) {

    return MessageFormat.format(
        """
  	        /**
  	         * Set a new value for the '{'@code {0}'}' record component in the builder
  	         */
  	        public {2}Builder {0}({1} {0}) '{'
  	            this.{0} = {0};
  	            return this;
  	        '}'

  	        /**
  	         * Return the current value for the '{'@code {0}'}' record component in the builder
  	         */
  	        public {1} {0}() '{'
  	            return {0};
  	        '}'

  	      """,
        componentName, type, shortName.replace(".", "$"));
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
