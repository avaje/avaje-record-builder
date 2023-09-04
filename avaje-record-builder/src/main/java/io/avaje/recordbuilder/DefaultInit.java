package io.avaje.recordbuilder;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target({RECORD_COMPONENT, PARAMETER})
public @interface DefaultInit {

  /** Specify how the default value should be initialized */
  String value();

  @Retention(SOURCE)
  @Target({TYPE, PACKAGE, MODULE})
  @interface Global {
    Class<?> type();

    /** Specify how the default value should be initialized */
    String value();
  }
}
