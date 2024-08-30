package io.avaje.recordbuilder.nullable;

import org.jspecify.annotations.NonNull;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Nullity(@NonNull String nonNull, String nully, String marked) {}
