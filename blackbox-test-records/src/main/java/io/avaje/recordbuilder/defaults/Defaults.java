package io.avaje.recordbuilder.defaults;

import java.util.List;

import io.avaje.recordbuilder.DefaultValue;
import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder
public record Defaults(
    @DefaultValue("List.of(1,2,3)") List<Integer> list,
    @DefaultValue("24") int num,
    @DefaultValue("\"string val\"") String str,
    @DefaultValue("CustomClass.createDefault()") CustomClass custom) {}
