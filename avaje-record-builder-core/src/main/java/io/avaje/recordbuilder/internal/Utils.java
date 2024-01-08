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

  /**
   * Return true if the element is non-nullable.
   *
   * @param prism
   */
  public static boolean isNonNullable(Element e, BuilderPrism prism) {

    for (final AnnotationMirror mirror : UType.parse(e.asType()).annotations()) {
      if (mirror.getAnnotationType().toString().endsWith("NonNull")) {
        return true;
      }
      if (mirror.getAnnotationType().toString().endsWith("Nullable")) {
        return false;
      }
    }
    return prism.enforceNullSafety();
  }
}
