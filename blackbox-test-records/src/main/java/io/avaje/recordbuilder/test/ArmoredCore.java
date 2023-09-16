package io.avaje.recordbuilder.test;

import io.avaje.recordbuilder.DefaultValue;

public record ArmoredCore(
    @DefaultValue("\"Steel Haze\"") String coreName,
    String model,
    int energyReserve,
    int ap) {}
