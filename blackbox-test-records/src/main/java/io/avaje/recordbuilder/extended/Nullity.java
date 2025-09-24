package io.avaje.recordbuilder.extended;

import org.jspecify.annotations.NonNull;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Nullity(@NonNull String nonNull, String nully, String marked) {}
