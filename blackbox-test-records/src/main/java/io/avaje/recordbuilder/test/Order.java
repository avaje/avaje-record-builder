package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

import java.util.List;

@RecordBuilder
public record Order(
        long id,
        Customer customer,
        List<OrderLine> lines
) {

    public static OrderBuilder builder() {
        return OrderBuilder.builder();
    }
}
