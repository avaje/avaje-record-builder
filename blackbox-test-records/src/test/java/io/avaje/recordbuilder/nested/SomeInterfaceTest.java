package io.avaje.recordbuilder.nested;

import io.avaje.recordbuilder.nested.SomeInterface.MyParams;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SomeInterfaceTest {

  @Test
  void test() {

    var params = MyParams.builder()
      .param1("p1")
      .param2("p2")
      .build();

    assertThat(params.param1()).isEqualTo("p1");
    assertThat(params.param2()).isEqualTo("p2");
  }
}
