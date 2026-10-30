package io.avaje.recordbuilder.internal;

import java.util.List;

import io.jstach.jstache.JStache;

public class Templates {
  private Templates() {}

  @JStache(
      template =
          """
      {{packageName}}

      {{imports}}

      /** Builder class for {@link {{shortName}} } */
      @SuppressWarnings("all")
      @Generated("avaje-record-builder")
      public class {{builderName}}{{fullTypeParams}} {

      {{fields}}
        private {{builderName}}() {}
             {{constructor}}
        /**
         * {{builderDoc}}
         */
        public static{{fullTypeParamsTransformed}}{{builderReturn}}{{typeParams}} builder() {
          return new {{builderNew}}{{typeParams}}();
        }

        /**
         * Return a new builder with all fields set to the values taken from the given record instance
         */
        public static{{fullTypeParamsTransformed}}{{builderName}}{{typeParams}} builder({{shortName}}{{typeParams}} from) {
          return new {{builderName}}{{typeParams}}({{builderFrom}});
        }

        /**
         * Return a new {{shortName}} instance with all fields set to the current values in this builder
         */
         public {{shortName}}{{typeParams}} build() {
           return new {{shortName}}{{typeParams}}({{build}});
         }

         private static <T> T requireNonNull(@Nullable T obj, String fieldName) {
           if (obj == null) {
             throw new IllegalStateException(
                 \"{{builderName}} expected a value for property %s, but was null.\".formatted(fieldName));
           }
           return obj;
         }
      {{stages}}""")
  public record ClassTemplate(
      String packageName,
      String imports,
      String builderName,
      String shortName,
      String fields,
      String constructor,
      String builderFrom,
      String build,
      String fullTypeParams,
      String fullTypeParamsTransformed,
      String typeParams,
      String builderNew,
      String builderReturn,
      String builderDoc,
      String stages) {
    String render() {
      return ClassTemplateRenderer.of().execute(this);
    }

    static String classTemplate(
        String packageName,
        String imports,
        String builderName,
        String shortName,
        String fields,
        String constructorArgs,
        String constructorBody,
        String builderFrom,
        String build,
        String fullTypeParams,
        String typeParams,
        List<String> stageNames,
        String stages) {

      var constructor =
          constructorArgs.isBlank()
              ? ""
              : new Constructor(builderName, constructorArgs, constructorBody).render();
      return new ClassTemplate(
              packageName.isBlank() ? "" : "package " + packageName + ";",
              imports,
              builderName,
              shortName,
              fields,
              constructor,
              builderFrom,
              build,
              fullTypeParams,
              fullTypeParams.transform(s -> s.isEmpty() ? " " : " " + s + " "),
              typeParams,
              stageNames.isEmpty() ? builderName : "Stages",
              stageNames.isEmpty() ? builderName : stageNames.get(0),
              stageNames.isEmpty()
                  ? "Return a new builder with all fields set to default Java values"
                  : "Return a new staged builder. Every component must be set in declaration order\n"
                      + "   * before {@code build()} can be called",
              stages)
          .render();
    }
  }

  @JStache(
      template =
          """

             private {{builderName}}({{args}}) {
               {{constructorBody}}
             }
           """)
  public record Constructor(String builderName, String args, String constructorBody) {

    String render() {
      return ConstructorRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """

             /** Set a new value for {@code {{componentName}} }. */
             public {{builderName}}{{typeParams}} {{componentName}}({{type}} {{componentName}}) {
               this.{{componentName}} = {{componentName}};
               return this;
             }
           """)
  public record MethodSetter(
      String componentName, String type, String builderName, String typeParams) {

    static String methodSetter(
        CharSequence componentName, String type, String builderName, String typeParams) {

      return new MethodSetter(
              componentName.toString(), type, builderName, typeParams)
          .render();
    }

    String render() {
      return MethodSetterRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """

               /** Return the current value for {@code {{typeName}} }. */
               public {{nullable}}{{typeName}} {{componentName}}() {
                 return {{componentName}};
               }
             """)
  public record MethodGetter(
      String nullable, String componentName, String typeName, String shortName) {

    static String methodGetter(CharSequence componentName, UType utype, String shortName) {

      var typeName = utype.shortWithoutAnnotations();
      var mainType = ProcessorUtils.shortType(utype.mainType());
      var index = mainType.lastIndexOf(".");
      var isNested = index != -1;
      if (isNested) {
        typeName = new StringBuilder(typeName).insert(index + 1, "@Nullable ").toString();
      }

      return new MethodGetter(
              isNested || Utils.isNullableType(utype.mainType()) ? "" : "@Nullable ",
              componentName.toString(),
              typeName,
              shortName.replace(".", "$"))
          .render();
    }

    String render() {
      return MethodGetterRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """

             /** Add new element to the {@code {{componentName}} } collection. */
             public {{builderName}}{{typeParams}} add{{upperCamel}}({{param0}} element) {
               this.{{componentName}}.add(element);
               return this;
             }
           """)
  public record MethodAdd(
      String componentName, String builderName, String upperCamel, String param0, String typeParams) {
    static String methodAdd(
        String componentName, String builderName, String param0, String typeParams) {
      String upperCamel =
          Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);

      return new MethodAdd(
              componentName, builderName, upperCamel, param0, typeParams)
          .render();
    }

    String render() {
      return MethodAddRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """

             /** Add new key/value pair to the {@code {{componentName}} } map. */
             public {{builderName}}{{typeParams}} put{{upperCamel}}({{param0}} key, {{param1}} value) {
               this.{{componentName}}.put(key, value);
               return this;
             }
           """)
  public record MethodPut(
      String componentName,
      String builderName,
      String upperCamel,
      String param0,
      String param1,
      String typeParams) {

    static String methodPut(
        String componentName, String builderName, String param0, String param1, String typeParams) {
      String upperCamel =
          Character.toUpperCase(componentName.charAt(0)) + componentName.substring(1);

      return new MethodPut(
              componentName, builderName, upperCamel, param0, param1, typeParams)
          .render();
    }

    String render() {
      return MethodPutRenderer.of().execute(this);
    }
  }

  @JStache(
      template =
          """

             /** Stage requiring a value for {@code {{componentName}} }. */
             public interface {{stageName}}{{fullTypeParams}} {
               {{next}}{{typeParams}} {{componentName}}({{type}} {{componentName}});{{collectionMethods}}
             }
           """)
  public record StageInterface(
      String stageName,
      String componentName,
      String type,
      String next,
      String fullTypeParams,
      String typeParams,
      String collectionMethods) {

    String render() {
      return StageInterfaceRenderer.of().execute(this);
    }
  }
}
