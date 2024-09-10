package io.avaje.recordbuilder.nested;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record TestRecord(String s) {
  @RecordBuilder
  public record NestedTestRecord(String s) {}
}
