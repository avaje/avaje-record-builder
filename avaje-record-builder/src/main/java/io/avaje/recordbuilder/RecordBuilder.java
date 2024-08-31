package io.avaje.recordbuilder;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** Generate a builder class for the given record */
@Documented
@Target(TYPE)
@Retention(SOURCE)
public @interface RecordBuilder {

  /** Whether getter methods should be generated on the builder */
  boolean getters() default false;

  @Retention(SOURCE)
  @Target({TYPE, PACKAGE, MODULE})
  @interface Import {

    /** Specify types to generate Builders for. */
    Class<? extends Record>[] value();

    /** Whether getter methods should be generated on the builder */
    boolean getters() default false;
  }
}
