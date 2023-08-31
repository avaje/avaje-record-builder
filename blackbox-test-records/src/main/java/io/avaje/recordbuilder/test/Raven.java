package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.DefaultInit;
import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Raven(
    int ap,
    String coreName,
    Weapon rArm,
    @DefaultInit("new Raven.Weapon(0, \"Pilebunker\", DamageType.EXPLOSIVE)") Weapon lArm,
    Weapon rShoulder,
    Weapon lShoulder) {

  @RecordBuilder
  public record Weapon(@DefaultInit("5_000") int cost, String name, DamageType type) {}
}
