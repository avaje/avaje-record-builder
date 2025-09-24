package io.avaje.recordbuilder.test.pkginfo;

public record Hornet(Silk silk) {

  static HornetBuilder builder() {
    return HornetBuilder.builder();
  }

  public record Silk(String song) {
    static Hornet$SilkBuilder builder() {
      return Hornet$SilkBuilder.builder();
    }
  }
}
