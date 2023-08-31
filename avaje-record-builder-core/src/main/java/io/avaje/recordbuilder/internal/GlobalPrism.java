package io.avaje.recordbuilder.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import javax.annotation.processing.Generated;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

/** A Prism representing a {@link io.avaje.recordbuilder.DefaultInit.Global @Global} annotation. */ 
@Generated("avaje-prism-generator")
final class GlobalPrism {
  /** store prism value of type */
  private final TypeMirror _type;

  /** store prism value of value */
  private final String _value;

  public static final String PRISM_TYPE = "io.avaje.recordbuilder.DefaultInit.Global";

  /**
   * An instance of the Values inner class whose
   * methods return the AnnotationValues used to build this prism. 
   * Primarily intended to support using Messager.
   */
  final Values values;

  /** Returns true if the mirror is an instance of {@link io.avaje.recordbuilder.DefaultInit.Global @Global} is present on the element, else false.
   *
   * @param mirror mirror. 
   * @return true if prism is present. 
   */
  static boolean isInstance(AnnotationMirror mirror) {
    return getInstance(mirror) != null;
  }

  /** Returns true if {@link io.avaje.recordbuilder.DefaultInit.Global @Global} is present on the element, else false.
   *
   * @param element element. 
   * @return true if annotation is present on the element. 
   */
  static boolean isPresent(Element element) {
    return getInstanceOn(element) != null;
  }

  /** Return a prism representing the {@link io.avaje.recordbuilder.DefaultInit.Global @Global} annotation present on the given element. 
   * similar to {@code element.getAnnotation(Global.class)} except that 
   * an instance of this class rather than an instance of {@link io.avaje.recordbuilder.DefaultInit.Global @Global}
   * is returned.
   *
   * @param element element. 
   * @return prism on element or null if no annotation is found. 
   */
  static GlobalPrism getInstanceOn(Element element) {
    final var mirror = getMirror(element);
    if (mirror == null) return null;
    return getInstance(mirror);
  }

  /** Return a Optional representing a nullable {@link io.avaje.recordbuilder.DefaultInit.Global @Global} annotation on the given element. 
   * similar to {@link element.getAnnotation(io.avaje.recordbuilder.DefaultInit.Global.class)} except that 
   * an Optional of this class rather than an instance of {@link io.avaje.recordbuilder.DefaultInit.Global}
   * is returned.
   *
   * @param element element. 
   * @return prism optional for element. 
   */
  static Optional<GlobalPrism> getOptionalOn(Element element) {
    final var mirror = getMirror(element);
    if (mirror == null) return Optional.empty();
    return getOptional(mirror);
  }

  /** Return a prism of the {@link io.avaje.recordbuilder.DefaultInit.Global @Global} annotation from an annotation mirror. 
   *
   * @param mirror mirror. 
   * @return prism for mirror or null if mirror is an incorrect type. 
   */
  static GlobalPrism getInstance(AnnotationMirror mirror) {
    if (mirror == null || !PRISM_TYPE.equals(mirror.getAnnotationType().toString())) return null;

    return new GlobalPrism(mirror);
  }

  /** Return an Optional representing a nullable {@link GlobalPrism @GlobalPrism} from an annotation mirror. 
   * similar to {@link e.getAnnotation(io.avaje.recordbuilder.DefaultInit.Global.class)} except that 
   * an Optional of this class rather than an instance of {@link io.avaje.recordbuilder.DefaultInit.Global @Global}
   * is returned.
   *
   * @param mirror mirror. 
   * @return prism optional for mirror. 
   */
  static Optional<GlobalPrism> getOptional(AnnotationMirror mirror) {
    if (mirror == null || !PRISM_TYPE.equals(mirror.getAnnotationType().toString())) return Optional.empty();

    return Optional.of(new GlobalPrism(mirror));
  }

  private GlobalPrism(AnnotationMirror mirror) {
    for (final ExecutableElement key : mirror.getElementValues().keySet()) {
      memberValues.put(key.getSimpleName().toString(), mirror.getElementValues().get(key));
    }
    for (final ExecutableElement member : ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements())) {
      defaults.put(member.getSimpleName().toString(), member.getDefaultValue());
    }
    _type = getValue("type", TypeMirror.class);
    _value = getValue("value", String.class);
    this.values = new Values(memberValues);
    this.mirror = mirror;
    this.isValid = valid;
  }

  /** 
   * Returns a TypeMirror representing the value of the {@code java.lang.Class<?> type()} member of the Annotation.
   * @see io.avaje.recordbuilder.DefaultInit.Global#type()
   */ 
  public TypeMirror type() { return _type; }

  /** 
   * Returns a String representing the value of the {@code java.lang.String value()} member of the Annotation.
   * @see io.avaje.recordbuilder.DefaultInit.Global#value()
   */ 
  public String value() { return _value; }

  /**
   * Determine whether the underlying AnnotationMirror has no errors.
   * True if the underlying AnnotationMirror has no errors.
   * When true is returned, none of the methods will return null.
   * When false is returned, a least one member will either return null, or another
   * prism that is not valid.
   */
   final boolean isValid;
    
  /**
   * The underlying AnnotationMirror of the annotation
   * represented by this Prism. 
   * Primarily intended to support using Messager.
   */
   final AnnotationMirror mirror;
  /**
   * A class whose members corespond to those of {@link io.avaje.recordbuilder.DefaultInit.Global @Global} 
   * but which each return the AnnotationValue corresponding to
   * that member in the model of the annotations. Returns null for
   * defaulted members. Used for Messager, so default values are not useful.
   */
  static final class Values {
    private final Map<String, AnnotationValue> values;

    private Values(Map<String, AnnotationValue> values) {
      this.values = values;
    }    
    /** Return the AnnotationValue corresponding to the type() 
     * member of the annotation, or null when the default value is implied.
     */
    AnnotationValue type(){ return values.get("type");}
    /** Return the AnnotationValue corresponding to the value() 
     * member of the annotation, or null when the default value is implied.
     */
    AnnotationValue value(){ return values.get("value");}
  }

  private final Map<String, AnnotationValue> defaults = new HashMap<String, AnnotationValue>(10);
  private final Map<String, AnnotationValue> memberValues = new HashMap<String, AnnotationValue>(10);
  private boolean valid = true;

  private <T> T getValue(String name, Class<T> clazz) {
    final T result = GlobalPrism.getValue(memberValues, defaults, name, clazz);
    if (result == null) valid = false;
    return result;
  }

  private static AnnotationMirror getMirror(Element target) {
    for (final var m : target.getAnnotationMirrors()) {
      final CharSequence mfqn = ((TypeElement) m.getAnnotationType().asElement()).getQualifiedName();
      if (PRISM_TYPE.contentEquals(mfqn)) return m;
    }
    return null;
  }

  private static <T> T getValue(Map<String, AnnotationValue> memberValues, Map<String, AnnotationValue> defaults, String name, Class<T> clazz) {
    AnnotationValue av = memberValues.get(name);
    if (av == null) av = defaults.get(name);
    if (av == null) {
      return null;
    }
    if (clazz.isInstance(av.getValue())) return clazz.cast(av.getValue());
    return null;
  }

}
