package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.Generated;
import io.avaje.recordbuilder.test.Address;
import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Consumer;

/** Builder class for {@link Customer} */
@Generated("avaje-record-builder")
public class CustomerBuilder {
  private long id;
  private String name;
  private Locale locale;
  private LocalDate startDate;
  private Address billingAddress;
  private Address shippingAddress;

  private CustomerBuilder() {
  }

  private CustomerBuilder(
      long id,
      String name,
      Locale locale,
      LocalDate startDate,
      Address billingAddress,
      Address shippingAddress) {
    this.id = id;
    this.name = name;
    this.locale = locale;
    this.startDate = startDate;
    this.billingAddress = billingAddress;
    this.shippingAddress = shippingAddress;
  }

  /**
   * Return a new builder with all fields set to default Java values
   */
  public static CustomerBuilder builder() {
      return new CustomerBuilder();
  }

  /**
   * Return a new builder with all fields set to the values taken from the given record instance
   */
  public static CustomerBuilder builder(Customer from) {
      return new CustomerBuilder(
        from.id(), from.name(), from.locale(), from.startDate(), from.billingAddress(), from.shippingAddress());
  }

  /**
   * Return a new Customer instance with all fields set to the current values in this builder
   */
  public Customer build() {
      return new Customer(id, name, locale, startDate, billingAddress, shippingAddress);
  }

  /**
   * Modify this builder with the given consumer
   */
  public CustomerBuilder transform(Consumer<CustomerBuilder> builderCustomer) {
      builderCustomer.accept(this);
      return this;
  }

  /** Set a new value for {@code id}. */
  public CustomerBuilder id(long id) {
      this.id = id;
      return this;
  }
