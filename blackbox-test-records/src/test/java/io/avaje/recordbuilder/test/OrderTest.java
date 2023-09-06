package io.avaje.recordbuilder.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class OrderTest {

  private final Product product93 = Product.builder()
    .id(93)
    .sku("HSDJ")
    .build();

  @Test
  void build() {

    var order = Order.builder()
      .id(42)
      .customer(Customer.builder().id(99).name("fred").build())
      .addLines(new OrderLine(42, product93, 1034))
      .addLines(new OrderLine(42, product93, 1034))
      .build();

    assertThat(order.id()).isEqualTo(42);
    assertThat(order.lines()).isNotEmpty();

    Order fromOrder = Order.from(order)
      .id(95)
      .build();

    assertThat(fromOrder.id()).isEqualTo(95);
    assertThat(fromOrder.customer()).isSameAs(order.customer());
  }

  @Test
  void transform() {
    var original = Order.builder()
      .id(42)
      .customer(Customer.builder().id(99).name("fred").build())
      .addLines(new OrderLine(42, product93, 1034))
      .addLines(new OrderLine(42, product93, 1034))
      .status(Order.Status.NEW)
      .build();

    Order transformedOrder =
      doSomeTransform(original)
      .build();

    assertThat(transformedOrder.status()).isEqualTo(Order.Status.COMPLETE);
  }

  private static OrderBuilder doSomeTransform(Order original) {
    OrderBuilder orderBuilder = Order.from(original);
    if (orderBuilder.status() == Order.Status.NEW) {
      orderBuilder.status(Order.Status.COMPLETE);

      // transform a nested collection
      List<OrderLine> newLines = orderBuilder.lines().stream()
        .filter(line -> line.id() < 43)
        .toList();

      orderBuilder.lines(newLines);
    }
    return orderBuilder;
  }
}
