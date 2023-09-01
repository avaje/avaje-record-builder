package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder(getters = true)
public record Address(
        String line1,
        String line2,
        String city,
        String country
) {

    /**
     * Return a new builder for Address
     */
    public static AddressBuilder builder() {
        return AddressBuilder.builder();
    }
}
