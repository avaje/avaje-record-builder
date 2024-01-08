package io.avaje.recordbuilder.defaults;

import java.util.List;

import io.avaje.recordbuilder.DefaultValue;
import io.avaje.recordbuilder.RecordBuilder;
import io.avaje.recordbuilder.test.SomeInterface;

@RecordBuilder(builderInterfaces = SomeInterface.class)
public record Defaults(
    @DefaultValue("List.of(1,2,3)") List<Integer> list,
    @DefaultValue("24") int num,
    @DefaultValue("\"string val\"") String str,
    @DefaultValue("defaultString()") String str2,
    @DefaultValue("CustomClass.createDefault()") CustomClass custom) {}
