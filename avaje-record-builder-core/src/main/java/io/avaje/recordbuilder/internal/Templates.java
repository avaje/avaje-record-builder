package io.avaje.recordbuilder.internal;

import java.text.MessageFormat;
import java.util.function.Consumer;

public class Templates {
  private Templates() {}

  static String classTemplate(
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

		   /** Builder class for '{'@link {2}'}' */
		   @Generated("avaje-record-builder")
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

  static String transformers(String shortName) {
    return MessageFormat.format(
        """
		     /**
		      * Modify this builder with the given consumer
		      */
		     public {0}Builder transform(Consumer<{0}Builder> builder{0}) '{'
		         builder{0}.accept(this);
		         return this;
		     '}'

		   """,
        shortName.replace(".", "$"));
  }

  static String methodSetter(CharSequence componentName, String type, String shortName) {
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

  static String methodGetter(CharSequence componentName, String type, String shortName) {
    return MessageFormat.format(
        """
		     /** Return the current value for '{'@code {0}'}'. */
		     public {1} {0}() '{'
		         return {0};
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"));
  }

  static String methodAdd(String componentName, String type, String shortName, String param0) {
    String upperCamel = Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);
    return MessageFormat.format(
        """

		     /** Add new element to the '{'@code {0}'}' collection. */
		     public {2}Builder add{3}({4} element) '{'
		         this.{0}.add(element);
		         return this;
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"), upperCamel, param0);
  }

  static String methodPut(
      String componentName, String type, String shortName, String param0, String param1) {
    String upperCamel = Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);
    return MessageFormat.format(
        """

		     /** Add new key/value pair to the '{'@code {0}'}' map. */
		     public {2}Builder put{3}({4} key, {5} value) '{'
		         this.{0}.add(element);
		         return this;
		     '}'
		   """,
        componentName, type, shortName.replace(".", "$"), upperCamel, param0, param1);
  }
}