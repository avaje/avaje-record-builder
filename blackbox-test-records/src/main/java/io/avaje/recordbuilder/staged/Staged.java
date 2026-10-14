package io.avaje.recordbuilder.staged;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

import io.avaje.recordbuilder.DefaultValue;
import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.recordbuilder.test.Address;

@RecordBuilder(staged = true)
public record Staged(
    String name,
    int age,
    @Nullable String nickname,
    Optional<String> motto,
    @DefaultValue("new java.util.ArrayList<>(List.of(\"x\"))") List<String> tags,
    Address address) {}
