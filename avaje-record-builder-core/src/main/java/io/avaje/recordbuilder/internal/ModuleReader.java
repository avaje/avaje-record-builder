package io.avaje.recordbuilder.internal;

import static io.avaje.recordbuilder.internal.APContext.logError;
import static io.avaje.recordbuilder.internal.APContext.logWarn;

import javax.lang.model.element.ModuleElement;

final class ModuleReader {
  private ModuleReader() {}

  private static ModuleElement module = APContext.getProjectModuleElement();

  static void read() {

    APContext.moduleInfoReader()
        .ifPresent(
            reader -> {
              for (var requires : reader.requires()) {
                if (requires.isStatic()
                    & requires
                        .getDependency()
                        .getSimpleName()
                        .contentEquals("io.avaje.recordbuilder")) {

                  logError(
                      "`requires io.avaje.recordbuilder` should be `requires static io.avaje.recordbuilder`",
                      module);
                }
                if (requires
                    .getDependency()
                    .getSimpleName()
                    .contentEquals("io.avaje.recordbuilder.core")) {

                  logWarn("io.avaje.recordbuilder.core should not be used", module);
                }
              }
            });
  }
}
