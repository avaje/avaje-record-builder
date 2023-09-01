package io.avaje.recordbuilder.test;

import java.util.*;
import java.util.OptionalInt;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Optionals(
    Optional<String> op, OptionalInt opInt, OptionalDouble opDouble, OptionalLong opLong) {}
