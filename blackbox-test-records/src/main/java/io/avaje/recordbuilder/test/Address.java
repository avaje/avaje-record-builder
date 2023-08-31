package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Address(
        String line1,
        String line2,
        String city,
        String country
) {
}
