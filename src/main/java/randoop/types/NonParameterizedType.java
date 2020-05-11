package randoop.types;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

/**
 * {@code NonParameterizedType} represents a non-parameterized class, interface, enum, or the
 * rawtype of a generic class. It is a wrapper for a {@link Class} object, which is a runtime
 * representation of a type.
 */
public class NonParameterizedType extends ClassOrInterfaceType {

  /** The runtime class of this simple type. */
  private final Class<?> runtimeType;

  /** A cache of all NonParameterizedTypes that have been created. */
  private static final @OrderNonDet Map<Class<?>, @Det NonParameterizedType> cache =
      new HashMap<>();

  /**
   * Create a {@link NonParameterizedType} object for the runtime class.
   *
   * @param runtimeType the runtime class for the type
   * @return a NonParameterizedType for the argument
   */
  public static @Det NonParameterizedType forClass(@Det Class<?> runtimeType) {
    @Det NonParameterizedType cached = cache.get(runtimeType);
    if (cached == null) {
      cached = new NonParameterizedType(runtimeType);
      cache.put(runtimeType, cached);
    }
    return cached;
  }

  /**
   * Create a {@link NonParameterizedType} object for the runtime class.
   *
   * @param runtimeType the runtime class for the type
   */
  public NonParameterizedType(Class<?> runtimeType) {
    assert !runtimeType.isPrimitive() : "must be reference type, got " + runtimeType.getName();
    this.runtimeType = runtimeType;
  }

  /**
   * {@inheritDoc}
   *
   * @return true if the runtime types are the same, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof NonParameterizedType)) {
      return false;
    }
    @SuppressWarnings("determinism") // casting here doesn't change the determinism type
    NonParameterizedType other = (NonParameterizedType) obj;
    return super.equals(obj) && this.runtimeType.equals(other.runtimeType);
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(runtimeType);
  }

  /**
   * {@inheritDoc}
   *
   * @return the name of this type
   * @see #getName()
   */
  @Override
  public String toString() {
    return this.getName();
  }

  @Override
  public @Det NonParameterizedType substitute(
      @Det NonParameterizedType this, @Det Substitution substitution) {
    return (NonParameterizedType)
        substitute(substitution, new NonParameterizedType(this.runtimeType));
  }

  @Override
  public @Det NonParameterizedType applyCaptureConversion(@Det NonParameterizedType this) {
    return (NonParameterizedType) applyCaptureConversion(this);
  }

  @Override
  public @Det List<ClassOrInterfaceType> getInterfaces(@Det NonParameterizedType this) {
    if (this.isRawtype()) {
      return this.getRawTypeInterfaces();
    }
    return getGenericInterfaces();
  }

  /**
   * Returns the list of direct interfaces for this class.
   *
   * @return the list of direct interfaces for this class or interface type
   */
  private List<ClassOrInterfaceType> getGenericInterfaces(@Det NonParameterizedType this) {
    List<ClassOrInterfaceType> interfaces = new ArrayList<>();
    for (java.lang.reflect.Type type : runtimeType.getGenericInterfaces()) {
      interfaces.add(ClassOrInterfaceType.forType(type));
    }
    return interfaces;
  }

  @Override
  public @Det NonParameterizedType getRawtype(@Det NonParameterizedType this) {
    return this;
  }

  /**
   * Returns the list of rawtypes for the direct interfaces for this type.
   *
   * @return the list of rawtypes for the direct interfaces of this type
   */
  private @Det List<ClassOrInterfaceType> getRawTypeInterfaces(@Det NonParameterizedType this) {
    @Det List<ClassOrInterfaceType> interfaces = new ArrayList<>();
    for (Class<?> c : runtimeType.getInterfaces()) {
      @Det Class<?> tmp = c;
      interfaces.add(NonParameterizedType.forClass(tmp));
    }
    return interfaces;
  }

  @Override
  public Class<?> getRuntimeClass() {
    return runtimeType;
  }

  @Override
  public ClassOrInterfaceType getSuperclass(@Det NonParameterizedType this) {
    if (this.isObject()) {
      return this;
    }
    if (this.isRawtype()) {
      Class<?> superclass = this.runtimeType.getSuperclass();
      if (superclass != null) {
        return NonParameterizedType.forClass(superclass);
      }
    } else {
      java.lang.reflect.Type supertype = this.runtimeType.getGenericSuperclass();
      if (supertype != null) {
        return ClassOrInterfaceType.forType(supertype);
      }
    }
    return JavaTypes.OBJECT_TYPE;
  }

  @Override
  public boolean isAbstract() {
    return Modifier.isAbstract(Modifier.classModifiers() & runtimeType.getModifiers());
  }

  /**
   * {@inheritDoc}
   *
   * <p>Specifically checks for <a
   * href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.7">boxing conversion
   * (section 5.1.7)</a>
   */
  @Override
  public boolean isAssignableFrom(@Det NonParameterizedType this, @Det Type sourceType) {
    // check identity and reference widening
    if (super.isAssignableFrom(sourceType)) {
      return true;
    }

    // otherwise, check for boxing conversion
    return sourceType.isPrimitive()
        && !sourceType.isVoid() // JLS doesn't say so, void is primitive
        && this.isAssignableFrom(((PrimitiveType) sourceType).toBoxedPrimitive());
  }

  @Override
  public boolean isBoxedPrimitive() {
    return PrimitiveTypes.isBoxedPrimitive(this.getRuntimeClass());
  }

  @Override
  public boolean isEnum() {
    return runtimeType.isEnum();
  }

  /**
   * {@inheritDoc}
   *
   * <p>For a {@link NonParameterizedType}, if this type instantiates the {@code otherType}, which
   * is a {@link NonParameterizedType} by {@link
   * ClassOrInterfaceType#isInstantiationOf(ReferenceType)} also checks that runtime classes are
   * equal. This allows for proper matching of member classes that are of {@link
   * NonParameterizedType}.
   */
  @Override
  public boolean isInstantiationOf(@Det NonParameterizedType this, @Det ReferenceType otherType) {
    boolean instantiationOf = super.isInstantiationOf(otherType);
    if ((otherType instanceof NonParameterizedType)) {
      return instantiationOf && this.runtimeClassIs(otherType.getRuntimeClass());
    }
    return instantiationOf;
  }

  @Override
  public boolean isInterface() {
    return runtimeType.isInterface();
  }

  @Override
  public boolean isRawtype() {
    return runtimeType.getTypeParameters().length > 0;
  }

  @Override
  public boolean isStatic() {
    return Modifier.isStatic(runtimeType.getModifiers() & Modifier.classModifiers());
  }

  /**
   * If this type is a boxed primitive, unboxes this type and returns the primitive type.
   *
   * @return the primitive type if this is a boxed primitive
   * @throws IllegalArgumentException if this is not a boxed primitive type
   */
  public @Det PrimitiveType toPrimitive(@Det NonParameterizedType this) {
    if (this.isBoxedPrimitive()) {
      Class<?> primitiveClass = PrimitiveTypes.toUnboxedType(this.getRuntimeClass());
      return PrimitiveType.forClass(primitiveClass);
    }
    throw new IllegalArgumentException("Type must be boxed primitive");
  }
}
