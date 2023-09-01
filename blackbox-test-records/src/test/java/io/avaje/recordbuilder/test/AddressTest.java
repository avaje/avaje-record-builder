package io.avaje.recordbuilder.test;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

  @Test
  void hasGetter() {

    var builder = Address.builder()
      .line1("1")
      .line2("2");

    assertThat(builder.line1()).isEqualTo("1");
    assertThat(builder.line2()).isEqualTo("2");

    Address address = builder.build();

    assertThat(address.line1()).isEqualTo("1");
    assertThat(address.line2()).isEqualTo("2");
  }
}
