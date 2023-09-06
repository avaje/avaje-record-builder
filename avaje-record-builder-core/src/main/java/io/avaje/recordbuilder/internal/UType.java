package io.avaje.recordbuilder.internal;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/** Utility type to help process {@link TypeMirror}s */
public interface UType {

  /**
   * Create a UType from the given TypeMirror.
   *
   * @param mirror type mirror to analyze
   * @return Create the UType from the given TypeMirror.
   */
  static UType parse(TypeMirror mirror) {

    return TypeMirrorVisitor.create(mirror);
  }

  /**
   * Return all the import types needed to write this mirror in source code (annotations included).
   *
   * @return Return the import types required.
   */
  Set<String> importTypes();

  /**
   * Return the full type as a code safe string. (with annotations if present)
   *
   * @return the full typeName
   */
  String full();

  /**
   *  Return the main type (outermost type). e.g for mirror {@ java.util.List<Something> you'll get java.util.List
   *
   * @return the outermost type
   */
  String mainType();

  /**
   * Return the full (but unqualified) type as a code safe string. Use in tandem with {@link
   * #importTypes()} to generate readable code
   *
   * @return the short name with unqualified type
   */
  String shortType();

  /** Return the first generic parameter. */
  default UType param0() {
    return null;
  }

  /** Return the second generic parameter. */
  default UType param1() {
    return null;
  }

  /**
   * Retrieve the component types associated with this mirror.
   *
   * <p>The annotated class must conform to the service provider specification. Specifically, it
   * must:
   *
   * <ul>
   *   <li>{@code TypeKind.ARRAY}: will contain the array componentType
   *   <li>{@code TypeKind.DECLARED}: will contain the generic parameters
   *   <li>{@code TypeKind.TYPEVAR}: will contain the upper bound for the type variable
   *   <li>{@code TypeKind.WILDCARD}: will contain the extends bound or super bound
   *   <li>{@code TypeKind.INTERSECTION}: will contain the bounds of the intersection
   * </ul>
   *
   * @return the component types
   */
  default List<UType> componentTypes() {
    return List.of();
  }

  /**
   * The kind of the type mirror used to create this Utype.
   *
   * @return the typekind
   */
  TypeKind kind();

  /**
   * Returns whether the type mirror is generic
   *
   * @return whether the type is generic
   */
  default boolean isGeneric() {
    return false;
  }

  /**
   * Return the annotation mirrors directly on the type.
   *
   * @return the annotations directly present
   */
  default List<AnnotationMirror> annotations() {
    return List.of();
  }

  /**
   * Return the annotation mirrors directly on the type and in within generic type use. e.g. for
   * {@code @NotEmpty Map<@Notblank String, Object>} you will get all the annotations not just
   *
   * @return all annotations present on this type
   */
  default List<AnnotationMirror> allAnnotationsInType() {
    return List.of();
  }

  /**
   * Return the full type as a string, stripped of annotations.
   *
   * @return full type, but without annotations
   */
  default String fullWithoutAnnotations() {
    return ProcessorUtils.trimAnnotations(full()).replace(",", ", ");
  }

  /**
   * Return the short type as a string, stripped of annotations.
   *
   * @return short type, but without annotations
   */
  default String shortWithoutAnnotations() {
    return ProcessorUtils.trimAnnotations(shortType()).replace(",", ", ");
  }
}
