package io.avaje.recordbuilder.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void build() {

        var product93 = Product.builder()
                .id(93)
                .sku("HSDJ")
                .build();

        var order = Order.builder()
                .id(42)
                .customer(Customer.builder().id(99).name("fred").build())
                //.addLine(new OrderLine(42, product93, 1034))
                //.addLine(new OrderLine(42, product93, 1034))
                .build();

        assertThat(order.id()).isEqualTo(42);
        //assertThat(order.lines()).isEmpty();

        Order fromOrder = Order.from(order)
                .id(95)
                .build();

        assertThat(fromOrder.id()).isEqualTo(95);
        assertThat(fromOrder.customer()).isSameAs(order.customer());
    }
}