package io.avaje.recordbuilder.test;

import java.util.Map;

import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.recordbuilder.test.Raven.Weapon;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;

@RecordBuilder
public record TypeUse(
    @NotEmpty @NotBlank Map<@NotBlank(groups = Weapon.class) String, @NotEmpty(groups = Weapon.class) Map<@NotBlank(groups = Weapon.class) Weapon, @NotBlank Raven>> map) {}
