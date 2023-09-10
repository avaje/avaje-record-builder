package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.DefaultValue;
import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.recordbuilder.test.Raven.Weapon;

@RecordBuilder
public record Raven(
  int ap,
  String coreName,
  Weapon rArm,
  //@DefaultInit("new Raven.Weapon(0, \"Pilebunker\", DamageType.EXPLOSIVE)")
  Weapon lArm,
  Weapon rShoulder,
 @DefaultValue("new Raven.Weapon(0, \"Pilebunker\", DamageType.EXPLOSIVE)")
  Weapon lShoulder) {

  @RecordBuilder // @DefaultValue("5_000")
  public record Weapon(int cost, String name, DamageType type) {
  }
}
