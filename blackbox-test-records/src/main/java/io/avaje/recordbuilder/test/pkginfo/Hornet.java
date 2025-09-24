package io.avaje.recordbuilder.test.pkginfo;

public record Hornet(Silk silk) {

  HornetBuilder builder() {
    return HornetBuilder.builder();
  }

  public record Silk(String song) {
    Hornet$SilkBuilder builder() {
      return Hornet$SilkBuilder.builder();
    }
  }
}
