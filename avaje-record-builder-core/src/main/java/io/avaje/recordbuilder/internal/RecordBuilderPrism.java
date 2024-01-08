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

/**
 * A Prism representing a {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder} annotation.
 */
@Generated("avaje-prism-generator")
final class RecordBuilderPrism implements BuilderPrism {
  /** store prism value of getters */
  private final Boolean _getters;

  /** store prism value of enforceNullSafety */
  private final Boolean _enforceNullSafety;

  /** store prism value of nullableAnnotation */
  private final TypeMirror _nullableAnnotation;

  /** store prism value of builderInterfaces */
  private final List<TypeMirror> _builderInterfaces;

  public static final String PRISM_TYPE = "io.avaje.recordbuilder.RecordBuilder";

  /**
   * An instance of the Values inner class whose methods return the AnnotationValues used to build
   * this prism. Primarily intended to support using Messager.
   */
  final Values values;

  /**
   * Returns true if the mirror is an instance of {@link
   * io.avaje.recordbuilder.RecordBuilder @RecordBuilder} is present on the element, else false.
   *
   * @param mirror mirror.
   * @return true if prism is present.
   */
  static boolean isInstance(AnnotationMirror mirror) {
    return getInstance(mirror) != null;
  }

  /**
   * Returns true if {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder} is present on the
   * element, else false.
   *
   * @param element element.
   * @return true if annotation is present on the element.
   */
  static boolean isPresent(Element element) {
    return getInstanceOn(element) != null;
  }

  /**
   * Return a prism representing the {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder}
   * annotation present on the given element. similar to {@code
   * element.getAnnotation(RecordBuilder.class)} except that an instance of this class rather than
   * an instance of {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder} is returned.
   *
   * @param element element.
   * @return prism on element or null if no annotation is found.
   */
  static RecordBuilderPrism getInstanceOn(Element element) {
    final var mirror = getMirror(element);
    if (mirror == null) return null;
    return getInstance(mirror);
  }

  /**
   * Return a Optional representing a nullable {@link
   * io.avaje.recordbuilder.RecordBuilder @RecordBuilder} annotation on the given element. similar
   * to {@code element.getAnnotation(io.avaje.recordbuilder.RecordBuilder.class)} except that an
   * Optional of this class rather than an instance of {@link io.avaje.recordbuilder.RecordBuilder}
   * is returned.
   *
   * @param element element.
   * @return prism optional for element.
   */
  static Optional<RecordBuilderPrism> getOptionalOn(Element element) {
    final var mirror = getMirror(element);
    if (mirror == null) return Optional.empty();
    return getOptional(mirror);
  }

  /**
   * Return a prism of the {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder} annotation
   * from an annotation mirror.
   *
   * @param mirror mirror.
   * @return prism for mirror or null if mirror is an incorrect type.
   */
  static RecordBuilderPrism getInstance(AnnotationMirror mirror) {
    if (mirror == null || !PRISM_TYPE.equals(mirror.getAnnotationType().toString())) return null;

    return new RecordBuilderPrism(mirror);
  }

  /**
   * Return an Optional representing a nullable {@link RecordBuilderPrism @RecordBuilderPrism} from
   * an annotation mirror. similar to {@code
   * e.getAnnotation(io.avaje.recordbuilder.RecordBuilder.class)} except that an Optional of this
   * class rather than an instance of {@link io.avaje.recordbuilder.RecordBuilder @RecordBuilder} is
   * returned.
   *
   * @param mirror mirror.
   * @return prism optional for mirror.
   */
  static Optional<RecordBuilderPrism> getOptional(AnnotationMirror mirror) {
    if (mirror == null || !PRISM_TYPE.equals(mirror.getAnnotationType().toString()))
      return Optional.empty();

    return Optional.of(new RecordBuilderPrism(mirror));
  }

  private RecordBuilderPrism(AnnotationMirror mirror) {
    for (final ExecutableElement key : mirror.getElementValues().keySet()) {
      memberValues.put(key.getSimpleName().toString(), mirror.getElementValues().get(key));
    }
    for (final ExecutableElement member :
        ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements())) {
      defaults.put(member.getSimpleName().toString(), member.getDefaultValue());
    }
    _getters = getValue("getters", Boolean.class);
    _enforceNullSafety = getValue("enforceNullSafety", Boolean.class);
    _nullableAnnotation = getValue("nullableAnnotation", TypeMirror.class);
    _builderInterfaces = getArrayValues("builderInterfaces", TypeMirror.class);
    this.values = new Values(memberValues);
    this.mirror = mirror;
    this.isValid = valid;
  }

  /**
   * Returns a Boolean representing the value of the {@code boolean public abstract boolean
   * getters() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder#getters()
   */
  @Override
  public Boolean getters() {
    return _getters;
  }

  /**
   * Returns a Boolean representing the value of the {@code boolean public abstract boolean
   * enforceNullSafety() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder#enforceNullSafety()
   */
  @Override
  public Boolean enforceNullSafety() {
    return _enforceNullSafety;
  }

  /**
   * Returns a TypeMirror representing the value of the {@code java.lang.Class<? extends
   * java.lang.annotation.Annotation> public abstract Class<? extends
   * java.lang.annotation.Annotation> nullableAnnotation() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder#nullableAnnotation()
   */
  @Override
  public TypeMirror nullableAnnotation() {
    return _nullableAnnotation;
  }

  /**
   * Returns a List&lt;TypeMirror&gt; representing the value of the {@code public abstract
   * Class<?>[] builderInterfaces() } member of the Annotation.
   *
   * @see io.avaje.recordbuilder.RecordBuilder#builderInterfaces()
   */
  @Override
  public List<TypeMirror> builderInterfaces() {
    return _builderInterfaces;
  }

  /**
   * Determine whether the underlying AnnotationMirror has no errors. True if the underlying
   * AnnotationMirror has no errors. When true is returned, none of the methods will return null.
   * When false is returned, a least one member will either return null, or another prism that is
   * not valid.
   */
  final boolean isValid;

  /**
   * The underlying AnnotationMirror of the annotation represented by this Prism. Primarily intended
   * to support using Messager.
   */
  final AnnotationMirror mirror;

  /**
   * A class whose members corespond to those of {@link
   * io.avaje.recordbuilder.RecordBuilder @RecordBuilder} but which each return the AnnotationValue
   * corresponding to that member in the model of the annotations. Returns null for defaulted
   * members. Used for Messager, so default values are not useful.
   */
  static final class Values {
    private final Map<String, AnnotationValue> values;

    private Values(Map<String, AnnotationValue> values) {
      this.values = values;
    }

    /**
     * Return the AnnotationValue corresponding to the getters() member of the annotation, or null
     * when the default value is implied.
     */
    AnnotationValue getters() {
      return values.get("getters");
    }

    /**
     * Return the AnnotationValue corresponding to the enforceNullSafety() member of the annotation,
     * or null when the default value is implied.
     */
    AnnotationValue enforceNullSafety() {
      return values.get("enforceNullSafety");
    }

    /**
     * Return the AnnotationValue corresponding to the nullableAnnotation() member of the
     * annotation, or null when the default value is implied.
     */
    AnnotationValue nullableAnnotation() {
      return values.get("nullableAnnotation");
    }

    /**
     * Return the AnnotationValue corresponding to the builderInterfaces() member of the annotation,
     * or null when the default value is implied.
     */
    AnnotationValue builderInterfaces() {
      return values.get("builderInterfaces");
    }
  }

  private final Map<String, AnnotationValue> defaults = new HashMap<>(10);
  private final Map<String, AnnotationValue> memberValues = new HashMap<>(10);
  private boolean valid = true;

  private <T> T getValue(String name, Class<T> clazz) {
    final T result = RecordBuilderPrism.getValue(memberValues, defaults, name, clazz);
    if (result == null) valid = false;
    return result;
  }

  private <T> List<T> getArrayValues(String name, final Class<T> clazz) {
    final List<T> result = RecordBuilderPrism.getArrayValues(memberValues, defaults, name, clazz);
    if (result == null) valid = false;
    return result;
  }

  private static AnnotationMirror getMirror(Element target) {
    for (final var m : target.getAnnotationMirrors()) {
      final CharSequence mfqn =
          ((TypeElement) m.getAnnotationType().asElement()).getQualifiedName();
      if (PRISM_TYPE.contentEquals(mfqn)) return m;
    }
    return null;
  }

  private static <T> T getValue(
      Map<String, AnnotationValue> memberValues,
      Map<String, AnnotationValue> defaults,
      String name,
      Class<T> clazz) {
    AnnotationValue av = memberValues.get(name);
    if (av == null) av = defaults.get(name);
    if (av == null) {
      return null;
    }
    if (clazz.isInstance(av.getValue())) return clazz.cast(av.getValue());
    return null;
  }

  private static <T> List<T> getArrayValues(
      Map<String, AnnotationValue> memberValues,
      Map<String, AnnotationValue> defaults,
      String name,
      final Class<T> clazz) {
    AnnotationValue av = memberValues.get(name);
    if (av == null) av = defaults.get(name);
    if (av == null) {
      return List.of();
    }
    if (av.getValue() instanceof List) {
      final List<T> result = new ArrayList<>();
      for (final var v : getValueAsList(av)) {
        if (clazz.isInstance(v.getValue())) {
          result.add(clazz.cast(v.getValue()));
        } else {
          return List.of();
        }
      }
      return result;
    } else {
      return List.of();
    }
  }

  @SuppressWarnings("unchecked")
  private static List<AnnotationValue> getValueAsList(AnnotationValue av) {
    return (List<AnnotationValue>) av.getValue();
  }
}
