package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.logWarn;

import java.io.BufferedReader;

import javax.lang.model.element.ModuleElement;

final class ModuleReader {
  private ModuleReader() {}

  private static ModuleElement module;

  static void read(BufferedReader reader) {
    reader
        .lines()
        .forEach(
            line -> {
              if (line.isBlank()) {
                return;
              }

              if (line.contains("io.avaje.recordbuilder") && !line.contains("static")) {
                logWarn(
                    "`requires io.avaje.recordbuilder` should be `requires static io.avaje.recordbuilder`",
                    module);
              }
              if (line.contains("io.avaje.recordbuilder.core")) {
                logWarn("io.avaje.recordbuilder.core should not be used", module);
              }
            });
  }
}
