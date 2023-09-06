package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.validation.constraints.NotNull;

import java.util.List;

@RecordBuilder(getters = true)
public record Order(
  long id,
  @NotNull Status status,
  Customer customer,
  List<OrderLine> lines
) {
  public enum Status {
    NEW,
    IN_PROGRESS,
    COMPLETE
  }
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
