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
@Target({TYPE, PACKAGE, MODULE})
@Retention(SOURCE)
public @interface RecordBuilder {

  /** Whether getter methods should be generated on the builder */
  boolean getters() default false;

  /**
   * Whether to generate a staged builder. When true, {@code builder()} returns a chain of stages
   * that forces every component to be set, in declaration order, before {@code build()} becomes
   * available.
   *
   * <p>Collection and Map stages also have add/put methods that stay on the stage, and a {@code
   * end<Name>()} method that moves to the next stage. {@link DefaultValue} is only permitted on
   * Collection and Map components.
   */
  boolean staged() default false;

  @Retention(SOURCE)
  @Target({TYPE, PACKAGE, MODULE})
  @interface Import {

    /** Specify types to generate Builders for. */
    Class<? extends Record>[] value();

    /** Whether getter methods should be generated on the builder */
    boolean getters() default false;

    /** Whether to generate a staged builder, see {@link RecordBuilder#staged()} */
    boolean staged() default false;
  }
}
