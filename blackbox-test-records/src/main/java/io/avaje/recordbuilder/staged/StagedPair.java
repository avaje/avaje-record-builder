package io.avaje.recordbuilder.staged;

import org.jspecify.annotations.Nullable;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder(staged = true)
public record StagedPair<A, B extends Comparable<B>>(A first, B second, @Nullable String label) {}
