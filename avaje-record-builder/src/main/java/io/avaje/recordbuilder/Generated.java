package io.avaje.recordbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks source code that has been generated.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Generated {

  /**
   * The name of the generator used to generate this source.
   */
  String value() default "";
}
