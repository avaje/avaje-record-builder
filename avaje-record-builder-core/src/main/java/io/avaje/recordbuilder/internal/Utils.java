package io.avaje.recordbuilder.internal;

import javax.lang.model.element.PackageElement;
import javax.lang.model.util.ElementFilter;

final class Utils {
  private Utils() {}

  static boolean isInUnnamedPackage(boolean isImported, final PackageElement packageElement) {
    var module = APContext.getProjectModuleElement();

    return packageElement.isUnnamed()
        || isImported
            && module.isUnnamed()
            && ElementFilter.packagesIn(module.getEnclosedElements()).stream()
                .allMatch(PackageElement::isUnnamed);
  }
}
