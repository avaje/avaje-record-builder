package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.Builder;

import java.time.LocalDate;
import java.util.Locale;

@Builder
public record Customer(
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
