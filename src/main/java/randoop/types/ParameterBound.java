package randoop.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.NoQualifierParameter;

/**
 * Represents a type bound on a type variable or wildcard occurring as a type parameter of a generic
 * class, interface, method or constructor.
 *
 * <p>Type bounds for explicitly defined type variables of generic declarations are defined in <a
 * href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.2">JLS section
 * 8.1.2</a> as
 *
 * <pre>
 *   TypeBound:
 *     extends TypeVariable
 *     extends ClassOrInterfaceType [ &amp; InterfaceType ]
 * </pre>
 *
 * Type bounds for wildcards may be any reference type, which includes type variables, so the {@link
 * ParameterBound} class hierarchy is simplified to use this {@link ReferenceType} objects as
 * bounds. Intersection types (which includes the greatest lower bound construction used in capture
 * conversion) are explicitly represented. And, recursive type bounds are avoided by holding any
 * {@code java.lang.reflect.Type} with variables as a {@link LazyParameterBound}.
 *
 * <p>Type parameters only have upper bounds, but variables introduced by capture conversion can
 * have lower bounds. This class and its subclasses can represent both, with the default lower bound
 * being {@link JavaTypes#NULL_TYPE}, and the default upperbound being {@link
 * JavaTypes#OBJECT_TYPE}.
 *
 * @see EagerReferenceBound
 * @see IntersectionTypeBound
 * @see LazyParameterBound
 */
@NoQualifierParameter(NonDet.class)
public abstract class ParameterBound {

  /**
   * Constructs a parameter bound given a {@link ReferenceType}.
   *
   * @param type the {@link ReferenceType}
   * @return a {@link EagerReferenceBound} with the given type
   */
  public static ParameterBound forType(ReferenceType type) {
    return new EagerReferenceBound(type);
  }

  /**
   * Creates a bound from the array of bounds of a {@code java.lang.reflect.TypeVariable}.
   *
   * <p>The bounds of a type parameter are restricted, but those of a wildcard may be any reference
   * type. See <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.1.2">JLS
   * section 8.1.2</a>.
   *
   * @param variableSet the set of variables affected by this bound
   * @param bounds the type bounds
   * @return the {@code ParameterBound} for the given types
   */
  static @PolyDet("up") ParameterBound forTypes(
      Set<java.lang.reflect.@PolyDet("down") TypeVariable<?>> variableSet,
      java.lang.reflect.@Det Type @Det [] bounds) {
    if (bounds == null) {
      throw new IllegalArgumentException("bounds must be non-null");
    }

    if (bounds.length == 1) {
      return ParameterBound.forType(variableSet, bounds[0]);
    } else {
      @PolyDet List<@PolyDet ParameterBound> boundList = new @PolyDet ArrayList<>();
      for (java.lang.reflect.Type type : bounds) {
        boundList.add(ParameterBound.forType(variableSet, type));
      }
      return new IntersectionTypeBound(boundList);
    }
  }

  /**
   * Creates a {@code ParameterBound} object from a single {@code java.lang.reflect.Type}. Tests for
   * types that are represented by {@code Class} objects, or {@code
   * java.lang.reflect.ParameterizedType} objects.
   *
   * @param variableSet the set of type variables bound by this type
   * @param type the type for type bound
   * @return a type bound that ensures the given type is satisfied as an upper bound
   */
  static @PolyDet("down") ParameterBound forType(
      Set<java.lang.reflect.@PolyDet("down") TypeVariable<?>> variableSet,
      java.lang.reflect.@Det Type type) {
    if (type instanceof java.lang.reflect.ParameterizedType) {
      if (!hasTypeVariable(type, variableSet)) {
        return new EagerReferenceBound(ParameterizedType.forType(type));
      }
      return new LazyParameterBound(type);
    }
    if (type instanceof Class<?>) {
      return new EagerReferenceBound(ClassOrInterfaceType.forType(type));
    }
    if (isTypeVariable(type)) {
      @Det TypeVariable eagerBound = TypeVariable.forType(type);
      return new EagerReferenceBound(eagerBound);
    }
    return new LazyParameterBound(type);
  }

  /**
   * Applies the given substitution to this type bound by replacing type variables.
   *
   * @param substitution the type substitution
   * @return this bound with the type after the substitution has been applied
   */
  public abstract @Det ParameterBound substitute(
      @Det ParameterBound this, @Det Substitution substitution);
  // public abstract ParameterBound substitute(Substitution substitution);

  /**
   * Applies a capture conversion to any wildcard arguments in the type of this bound.
   *
   * @return this type with any wildcards replaced by capture conversion
   * @see ReferenceType#applyCaptureConversion()
   */
  public abstract @Det ParameterBound applyCaptureConversion(@Det ParameterBound this);

  /**
   * Returns any type parameters in the type of this bound.
   *
   * @return the list of {@code TypeVariable} objects in this bound
   */
  public abstract List<@PolyDet TypeVariable> getTypeParameters();

  /**
   * Indicates whether the given (reflection) type reference represents a type in which a type
   * variable occurs.
   *
   * @param type the reflection type
   * @param variableSet the set of variables
   * @return true if the type has a type variable, and false otherwise
   */
  private static boolean hasTypeVariable(
      java.lang.reflect.@PolyDet("down") Type type,
      Set<java.lang.reflect.@PolyDet("down") TypeVariable<?>> variableSet) {
    if (isTypeVariable(type)) {
      java.lang.reflect.TypeVariable<?> variable = (java.lang.reflect.TypeVariable) type;
      if (variableSet.contains(variable)) {
        return true;
      }
      @PolyDet("upDet") Set<java.lang.reflect.@PolyDet("down") TypeVariable<?>> recursiveSet =
          new HashSet<>(variableSet);
      recursiveSet.add(variable);
      for (java.lang.reflect. @PolyDet("down") Type boundType : variable.getBounds()) {
        java.lang.reflect.@PolyDet("down") Type tmp = boundType;
        if (hasTypeVariable(tmp, recursiveSet)) {
          return true;
        }
      }
    }
    if (type instanceof java.lang.reflect.ParameterizedType) {
      java.lang.reflect.ParameterizedType pt = (java.lang.reflect. @PolyDet("down") ParameterizedType) type;
      for (java.lang.reflect. @PolyDet("down") Type argType : pt.getActualTypeArguments()) {
        java.lang.reflect.@PolyDet("down") Type tmp = argType;
        if (hasTypeVariable(tmp, variableSet)) {
          return true;
        }
      }
    }
    if (type instanceof java.lang.reflect.WildcardType) {
      java.lang.reflect.WildcardType wt = (java.lang.reflect.WildcardType) type;
      for (java.lang.reflect. @PolyDet("down") Type boundType : wt.getUpperBounds()) {
        java.lang.reflect.@PolyDet("down") Type tmp = boundType;
        if (hasTypeVariable(tmp, variableSet)) {
          return true;
        }
      }
      for (java.lang.reflect. @PolyDet("down") Type boundType : wt.getLowerBounds()) {
        java.lang.reflect.@PolyDet("down") Type tmp = boundType;
        if (hasTypeVariable(tmp, variableSet)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Indicates whether the type is a type variable.
   *
   * @param type the type to test
   * @return true if the type is a type variable, false otherwise
   */
  static boolean isTypeVariable(java.lang.reflect.Type type) {
    return type instanceof java.lang.reflect.TypeVariable;
  }

  /**
   * Indicates whether the type of this bound has a wildcard type argument.
   *
   * @return true, if this bound has a wildcard argument, and false otherwise
   */
  abstract boolean hasWildcard();

  /**
   * Indicates whether the type of this bound has a capture variable.
   *
   * @return true iff this bound has a capture variable
   */
  abstract boolean hasCaptureVariable();

  /**
   * Indicates whether the type of this bound is generic.
   *
   * @return true, if this bound type is generic, and false otherwise
   */
  final boolean isGeneric() {
    return isGeneric(false);
  }

  /**
   * Indicates whether the type of this bound is generic.
   *
   * @param ignoreWildcards if true, ignore wildcards; that is, treat wildcards as not making the
   *     operation generic
   * @return true, if this bound type is generic, and false otherwise
   */
  public abstract boolean isGeneric(boolean ignoreWildcards);

  /**
   * Indicates whether this bound is a lower bound of the given argument type.
   *
   * @param argType the concrete argument type
   * @param subst the substitution
   * @return true if this bound is a subtype of the given type
   */
  public abstract boolean isLowerBound(
      @Det ParameterBound this, @Det Type argType, @Det Substitution subst);

  /**
   * Tests whether this is a lower bound on the type of a given bound with respect to a type
   * substitution. The body is approximately:
   *
   * <pre>{@code return this.substitute(substitution).isLowerBound(bound.substitute(substitution);}
   * </pre>
   *
   * @param bound the other bound
   * @param substitution the type substitution
   * @return true iff this bound is a lower bound on the type of the given bound
   */
  boolean isLowerBound(
      @Det ParameterBound this, @Det ParameterBound bound, @Det Substitution substitution) {
    return false;
  }

  /**
   * Indicate whether this bound is {@code Object}.
   *
   * @return true if this bound is {@code Object}, false otherwise
   */
  public abstract boolean isObject();

  /**
   * Indicates whether the type of this bound is a subtype of the type of the given bound.
   *
   * @param boundType the other bound
   * @return true if this type is a subtype of the other bound, false otherwise
   */
  public abstract boolean isSubtypeOf(@Det ParameterBound this, @Det ParameterBound boundType);

  /**
   * Determines if this bound is an upper bound for the argument type.
   *
   * @param argType the concrete argument type
   * @param subst the substitution
   * @return true if this bound is satisfied by the concrete type when the substitution is used on
   *     the bound, false otherwise
   */
  public abstract boolean isUpperBound(
      @Det ParameterBound this, @Det Type argType, @Det Substitution subst);

  /**
   * Indicates whether this bound is an upper bound on the type of the given bound with respect to
   * the type substitution.
   *
   * @param bound the other bound
   * @param substitution the type substitution
   * @return true if this bound is an upper bound on the type of the given bound, false otherwise
   */
  abstract boolean isUpperBound(
      @Det ParameterBound this, @Det ParameterBound bound, @Det Substitution substitution);

  /**
   * Indicates whether this bound is a type variable.
   *
   * @return true if this bound is a type variable, false otherwise
   */
  public boolean isVariable() {
    return false;
  }

  @Override
  public abstract String toString();
}
