package io.avaje.recordbuilder.internal;

import javax.lang.model.element.AnnotationMirror;
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

  /** Return true if the element has a NonNullable annotation. */
  public static boolean isNonNullable(Element p) {
    for (final AnnotationMirror mirror : p.getAnnotationMirrors()) {
      if (mirror.getAnnotationType().toString().endsWith("NonNull")) {
        return true;
      }
    }
    return false;
  }

  /** Return true if the element has a Nullable annotation. */
  public static boolean isNullable(Element p) {
    for (final AnnotationMirror mirror : p.getAnnotationMirrors()) {
      if (mirror.getAnnotationType().toString().endsWith("Nullable")) {
        return true;
      }
    }
    return false;
  }
}
