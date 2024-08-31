package io.avaje.recordbuilder.internal;

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
}
