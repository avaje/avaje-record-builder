package io.avaje.recordbuilder.internal;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;

interface UType {

  /** Create the UType from the given TypeMirror. */
  static UType parse(TypeMirror returnType) {

    return TypeMirrorVisitor.create(returnType);
  }

  /** Return the import types. */
  Set<String> importTypes();

  /** Return the short name. */
  String shortType();

  /** Return the main type (outer most type). */
  String mainType();

  /** Return the first generic parameter. */
  default UType param0() {
    return null;
  }

  /** Return the second generic parameter. */
  default UType param1() {
    return null;
  }

  /** Return the raw generic parameter if this UType is a Collection. */
  default UType paramRaw() {
    return null;
  }

  /** Return the raw type. */
  String full();

  default boolean isGeneric() {
    return false;
  }

  /** Return the generic param UType for the parameters. */
  default List<UType> genericParams() {
    return List.of();
  }
  /** Return the annonataion mirrors directly on the type. */
  default List<AnnotationMirror> annotations() {
    return List.of();
  }

  /** Return the annonataion mirrors directly on the type and in generic type use. */
  default List<AnnotationMirror> allAnnotationsInType() {
    return List.of();
  }
}
