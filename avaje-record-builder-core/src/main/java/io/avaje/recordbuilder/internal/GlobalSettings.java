package io.avaje.recordbuilder.internal;

import java.util.Optional;

import javax.lang.model.element.Element;

public final class GlobalSettings {

  private static final ThreadLocal<Ctx> CTX = new ThreadLocal<>();

  private GlobalSettings() {}

  public static final class Ctx {
    private boolean getters;
    private boolean initialized;
    private boolean enforceNullSafety;
    private String nullableAnnotation;

    public Ctx() {}
  }

  public static void init() {
    CTX.set(new Ctx());
  }

  public static void clear() {
    CTX.remove();
  }

  public static void configure(GlobalConfigPrism prism) {
    CTX.get().getters = prism.getters();
    CTX.get().enforceNullSafety = prism.enforceNullSafety();
    CTX.get().nullableAnnotation = prism.nullableAnnotation().toString();
    CTX.get().initialized = true;
  }

  public static boolean initialized() {
    return CTX.get().initialized;
  }

  public static void nullMarked() {
    if (NullMarkedPrism.isPresent(APContext.getProjectModuleElement())) {
      CTX.get().enforceNullSafety = true;
    }
  }

  public static boolean getters() {
    return CTX.get().getters;
  }

  public static boolean enforceNullSafety(Element e) {
    if (CTX.get().enforceNullSafety) {
      return true;
    }

    return checkNullMarked(e);
  }

  private static boolean checkNullMarked(Element e) {
    var enclosing = e.getEnclosingElement();
    if (enclosing == null) {
      return false;
    } else if (NullMarkedPrism.isPresent(enclosing) || NonNullApiPrism.isPresent(enclosing)) {
      return true;
    }
    return checkNullMarked(enclosing);
  }

  public static Optional<String> nullableAnnotation() {
    return Optional.ofNullable(CTX.get().nullableAnnotation);
  }
}
