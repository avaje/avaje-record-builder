package io.avaje.recordbuilder.defaults;

public class CustomClass {

  private CustomClass() {}

  public static CustomClass createDefault() {
    return new CustomClass();
  }
}
