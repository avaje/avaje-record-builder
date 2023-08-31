package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

import java.time.LocalDate;
import java.util.Locale;

@RecordBuilder
public record Customer (
        long id,
        String name,
        Locale locale,
        LocalDate startDate,
        Address billingAddress,
        Address shippingAddress
) {

    public static CustomerBuilder builder() {
        return CustomerBuilder.builder();
    }
}
