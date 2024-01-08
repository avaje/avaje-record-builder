package io.avaje.recordbuilder.nullable;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Nullity(@NonNull String nonNull, @Nullable String nully) {}
