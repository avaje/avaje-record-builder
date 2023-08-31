package io.avaje.recordbuilder.test;

public record OrderLine(
        long id,
        Product product,
        long orderQuantity
) {
}
