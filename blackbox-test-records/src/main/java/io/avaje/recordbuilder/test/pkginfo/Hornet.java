package io.avaje.recordbuilder.test.pkginfo;

public record Hornet(Silk silk) {
  public record Silk(String song) {}
}
