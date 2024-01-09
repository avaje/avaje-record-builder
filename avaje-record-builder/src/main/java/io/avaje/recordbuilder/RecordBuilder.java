package io.avaje.recordbuilder;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jspecify.annotations.Nullable;

/** Generate a builder class for the given record */
@Documented
@Target(TYPE)
@Retention(SOURCE)
public @interface RecordBuilder {

  /** Whether getter methods should be generated on the builder */
  boolean getters() default false;

  /**
   * Whether generated setter methods will enforce null safety for all members (excluding any
   * annotated with any form of {@code @Nullable}). Has the same effect as annotating each component
   * with {@code @NonNull}
   */
  boolean enforceNullSafety() default false;

  /** What nullable annotation to use for the builder fields. */
  Class<? extends Annotation> nullableAnnotation() default Nullable.class;

  /** The interfaces the generated builder will extend. */
  Class<?>[] builderInterfaces() default {};

  @Retention(SOURCE)
  @Target({TYPE, PACKAGE, MODULE})
  @interface Import {

    /** Specify types to generate Builders for. */
    Class<? extends Record>[] value();

    /** Whether getter methods should be generated on the builder */
    boolean getters() default false;

    /**
     * Whether generated setter methods will enforce null safety for all members (excluding any
     * annotated with any form of {@code @Nullable}). Has the same effect as annotating each
     * component with {@code @NonNull}
     */
    boolean enforceNullSafety() default false;

    /** What nullable annotation to use for the builder fields. */
    Class<? extends Annotation> nullableAnnotation() default Nullable.class;

    /** The interfaces the generated builder will extend. */
    Class<?>[] builderInterfaces() default {};
  }

  @Retention(SOURCE)
  @Target({PACKAGE, MODULE})
  @interface GlobalConfig {

    /** Whether getter methods should be generated on the builder */
    boolean getters() default false;

    /**
     * Whether generated setter methods will enforce null safety for all members (excluding any
     * annotated with any form of {@code @Nullable}). Has the same effect as annotating each
     * component with {@code @NonNull}
     */
    boolean enforceNullSafety() default false;

    /** What nullable annotation to use for the builder fields. */
    Class<? extends Annotation> nullableAnnotation() default Nullable.class;
  }
}
