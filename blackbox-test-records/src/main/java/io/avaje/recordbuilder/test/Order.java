package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.Builder;

import java.util.List;

@Builder
public record Order(
  long id,
  Customer customer,
  List<OrderLine> lines
) {

  /**
   * Return a new builder for Order
   */
  public static OrderBuilder builder() {
    return OrderBuilder.builder();
  }

  public static OrderBuilder from(Order source) {
    return OrderBuilder.builder(source);
  }

}
