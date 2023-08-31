package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Raven(
    int ap, String coreName, Weapon rArm, Weapon lArm, Weapon rShoulder, Weapon lShoulder) {

  @RecordBuilder
  public record Weapon(int ammo, String name, DamageType type) {}
}
