package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.Builder;

@Builder
public record Raven(
  int ap,
  String coreName,
  Weapon rArm,
  //@DefaultInit("new Raven.Weapon(0, \"Pilebunker\", DamageType.EXPLOSIVE)")
  Weapon lArm,
  Weapon rShoulder,
  Weapon lShoulder) {

  @Builder // @DefaultInit("5_000")
  public record Weapon(int cost, String name, DamageType type) {
  }
}
