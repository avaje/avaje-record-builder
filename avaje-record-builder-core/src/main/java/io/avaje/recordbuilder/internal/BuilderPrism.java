package io.avaje.recordbuilder.internal;

import java.util.List;

import javax.lang.model.type.TypeMirror;

public interface BuilderPrism {

  default boolean imported() {
    return this instanceof ImportPrism;
  }

  /**
   * Returns a Boolean representing the value of the {@code boolean public abstract boolean
   * getters() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder.Import#getters()
   */
  Boolean getters();

  /**
   * Returns a Boolean representing the value of the {@code boolean public abstract boolean
   * enforceNullSafety() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder.Import#enforceNullSafety()
   */
  Boolean enforceNullSafety();

  /**
   * Returns a TypeMirror representing the value of the {@code java.lang.Class<? extends
   * java.lang.annotation.Annotation> public abstract Class<? extends
   * java.lang.annotation.Annotation> nullableAnnotation() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder.Import#nullableAnnotation()
   */
  TypeMirror nullableAnnotation();

  /**
   * Returns a List&lt;TypeMirror&gt; representing the value of the {@code public abstract
   * Class<?>[] builderInterfaces() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder.Import#builderInterfaces()
   */
  List<TypeMirror> builderInterfaces();
}
