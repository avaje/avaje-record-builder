package io.avaje.recordbuilder.internal;

import java.text.MessageFormat;

public class Templates {
  private Templates() {}

  static String classTemplate(
      String packageName,
      String imports,
      String shortName,
      String implementsStr,
      String fields,
      String constructor,
      String constructorBody,
      String builderFrom,
      String build,
      String fullTypeParams,
      String typeParams) {

    return MessageFormat.format(
        """
		   {0}

		   {1}

		   /** Builder class for '{'@link {2}'}' */
		   @Generated("avaje-record-builder")
		   public class {2}Builder{8} %s'{'

		   {3}

		     private {2}Builder() '{'
		     '}'
		   """.formatted(implementsStr)
            + constructor(constructor)
            + """

		     /**
		      * Return a new builder with all fields set to default Java values
		      */
		     public static{9}{2}Builder{10} builder() '{'
		       return new {2}Builder{10}();
		     '}'

		     /**
		      * Return a new builder with all fields set to the values taken from the given record instance
		      */
		     public static{9}{2}Builder{10} builder({2}{10} from) '{'
		       return new {2}Builder{10}({6});
		     '}'

		     /**
		      * Return a new {2} instance with all fields set to the current values in this builder
		      */
		     public {2}{10} build() '{'
		       return new {2}{10}({7});
		     '}'

		     private static <T> T requireNonNull(@Nullable T obj) '{'
		       if (obj == null) '{'
		         throw new IllegalStateException(\"{2}Builder expects all nonnull values to not be null \");
		       '}'
		       return obj;
		     '}'

		   """,
        packageName.isBlank() ? "" : "package " + packageName + ";",
        imports,
        shortName,
        fields,
        constructor,
        constructorBody,
        builderFrom,
        build,
        fullTypeParams,
        fullTypeParams.transform(s -> s.isEmpty() ? " " : " " + s + " "),
        typeParams);
  }

  private static String constructor(String constructor) {

    return constructor.isBlank()
        ? ""
        : """

		     private {2}Builder({4}) '{'
		       {5}
		     '}'
		   """;
  }

  static String methodSetter(
      CharSequence componentName, String type, String shortName, String typeParams) {

    return MessageFormat.format(
        """

		     /** Set a new value for '{'@code {0}'}'. */
		     public {2}Builder{3} {0}({1} {0}) '{'
		       this.{0} = {0};
		       return this;
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"), typeParams);
  }

  static String methodGetter(CharSequence componentName, String type, String shortName) {
    var typeName = type;
    var index = typeName.lastIndexOf(".");
    var isNested = index != -1;
    if (isNested) {
      typeName = new StringBuilder(typeName).insert(index + 1, "@Nullable ").toString();
    }

    return MessageFormat.format(
        """

		     /** Return the current value for '{'@code {0}'}'. */{0}
		     public {2} {1}() '{'
		       return {1};
		     '}'
		   """,
        isNested || Utils.isNullable(type) ? "" : "\n   @Nullable",
        componentName,
        typeName,
        shortName.replace(".", "$"));
  }

  static String methodAdd(
      String componentName, String type, String shortName, String param0, String typeParams) {
    String upperCamel = Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);
    return MessageFormat.format(
        """

		     /** Add new element to the '{'@code {0}'}' collection. */
		     public {2}Builder{5} add{3}({4} element) '{'
		       this.{0}.add(element);
		       return this;
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"), upperCamel, param0, typeParams);
  }

  static String methodPut(
      String componentName,
      String type,
      String shortName,
      String param0,
      String param1,
      String typeParams) {
    String upperCamel = Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);
    return MessageFormat.format(
        """

		     /** Add new key/value pair to the '{'@code {0}'}' map. */
		     public {2}Builder{6} put{3}({4} key, {5} value) '{'
		       this.{0}.put(key, value);
		       return this;
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"), upperCamel, param0, param1, typeParams);
  }
}
