package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.Generated;
import java.util.function.Consumer;

/** Builder class for {@link Address} */
@Generated("avaje-record-builder")
public class AddressBuilder {
  private String line1;
  private String line2;
  private String city;
  private String country;

  private AddressBuilder() {
  }

  private AddressBuilder(String line1, String line2, String city, String country) {
    this.line1 = line1;
    this.line2 = line2;
    this.city = city;
    this.country = country;
  }

  /**
   * Return a new builder with all fields set to default Java values
   */
  public static AddressBuilder builder() {
      return new AddressBuilder();
  }

  /**
   * Return a new builder with all fields set to the values taken from the given record instance
   */
  public static AddressBuilder builder(Address from) {
      return new AddressBuilder(from.line1(), from.line2(), from.city(), from.country());
  }

  /**
   * Return a new Address instance with all fields set to the current values in this builder
   */
  public Address build() {
      return new Address(line1, line2, city, country);
  }

  /**
   * Modify this builder with the given consumer
   */
  public AddressBuilder transform(Consumer<AddressBuilder> builderAddress) {
      builderAddress.accept(this);
      return this;
  }

  /** Set a new value for {@code line1}. */
  public AddressBuilder line1(String line1) {
      this.line1 = line1;
      return this;
  }
  /** Return the current value for {@code line1}. */
  public String line1() {
      return line1;
  }
  /** Set a new value for {@code line2}. */
  public AddressBuilder line2(String line2) {
      this.line2 = line2;
      return this;
  }
  /** Return the current value for {@code line2}. */
  public String line2() {
      return line2;
  }
  /** Set a new value for {@code city}. */
  public AddressBuilder city(String city) {
      this.city = city;
      return this;
  }
  /** Return the current value for {@code city}. */
  public String city() {
      return city;
  }
  /** Set a new value for {@code country}. */
  public AddressBuilder country(String country) {
      this.country = country;
      return this;
  }
  /** Return the current value for {@code country}. */
  public String country() {
      return country;
  }
}