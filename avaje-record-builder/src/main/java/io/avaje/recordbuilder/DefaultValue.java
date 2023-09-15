package io.avaje.recordbuilder;

import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.RECORD_COMPONENT;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.avaje.recordbuilder.DefaultValue.Global.List;

@Retention(SOURCE)
@Target({RECORD_COMPONENT})
public @interface DefaultValue {

  /** String specifying how the default value should be initialized */
  String value();

  @Retention(SOURCE)
  @Target({TYPE, PACKAGE, MODULE})
  @Repeatable(List.class)
  @interface Global {

    /** The type that should have it's default value changed globally */
    Class<?> type();

    /** String specifying how the default value should be initialized */
    String value();

    @Retention(SOURCE)
    @Target({TYPE, PACKAGE, MODULE})
    @interface List {

      Global[] value();
    }
  }
}
