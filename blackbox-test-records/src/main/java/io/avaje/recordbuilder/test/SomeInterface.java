package io.avaje.recordbuilder.test;

public interface SomeInterface {

  default String defaultString() {
    return "somethin";
  }
}
