package io.avaje.recordbuilder.test;

import java.util.Map;

import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.NotEmpty;

@RecordBuilder
public record TypeUse(@NotEmpty Map<@NotBlank String, @NotBlank String> map) {}
