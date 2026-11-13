package io.avaje.recordbuilder.staged;

import java.util.List;
import java.util.Map;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder(staged = true)
public record StagedCollections(List<String> tags, Map<String, Integer> counts) {}
