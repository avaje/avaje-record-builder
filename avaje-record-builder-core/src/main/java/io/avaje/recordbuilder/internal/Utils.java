package io.avaje.recordbuilder.internal;

import javax.lang.model.element.Element;
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

  /**
   * Return true if the element is non-nullable.
   *
   * @param prism
   */
  public static boolean isNonNullable(Element e) {

    for (var mirror : UType.parse(e.asType()).annotations()) {
      if (mirror.getAnnotationType().toString().endsWith("Nullable")) {
        return false;
      } else if (NonNullPrism.isInstance(mirror)) {
        return true;
      }
    }

    return checkNullMarked(e);
  }

  private static boolean checkNullMarked(Element e) {
    if (e == null || NullUnmarkedPrism.isPresent(e)) {
      return false;
    } else if (NullMarkedPrism.isPresent(e)) {
      return true;
    }
    return checkNullMarked(e.getEnclosingElement());
  }

  public static boolean isNullableType(String type) {
    return InitMap.get(type) != null;
  }
}
