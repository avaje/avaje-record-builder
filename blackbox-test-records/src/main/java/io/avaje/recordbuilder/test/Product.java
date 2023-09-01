package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Product(
  long id,
  String sku,
  String name,
  String description,
  String packaging
) {

  public static ProductBuilder builder() {
    return ProductBuilder.builder();
  }
}
