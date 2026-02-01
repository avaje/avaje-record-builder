package io.avaje.recordbuilder.nested;

import io.avaje.recordbuilder.RecordBuilder;

public interface SomeInterface {

  @RecordBuilder
  record MyParams(
    String param1,
    String param2
  ) {

    public static MyParamsBuilder builder() {
      // builder is called MyParamsBuilder rather than SomeInterface$MyParamsBuilder
      // ... because the enclosed type is not a record
      return MyParamsBuilder.builder();
    }
  }
}
