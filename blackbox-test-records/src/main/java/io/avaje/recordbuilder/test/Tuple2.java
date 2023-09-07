package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder(getters = true)
record Tuple2<T1 extends SomeInterface & SomeInterface2, T2>(T1 t1, T2 t2) {}
