package randoop.types;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.main.RandoopBug;

/**
 * A lazy representation of a type bound in which a type variable occurs. Prevents type recognition
 * from having to deal with recursive type bounds. All methods that need to evaluate the bound are
 * provided with a substitution for the variable for which this object is a bound.
 */
class LazyParameterBound extends ParameterBound {

  /** the type for this bound */
  private final java.lang.reflect.Type boundType;

  /**
   * Creates a {@code LazyParameterBound} from the given rawtype and type parameters.
   *
   * @param boundType the reflection type for this bound
   */
  LazyParameterBound(java.lang.reflect.Type boundType) {
    this.boundType = boundType;
  }

  /**
   * {@inheritDoc}
   *
   * @return true if argument is a {@code LazyParameterBound}, and the rawtype and parameters are
   *     identical, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof LazyParameterBound)) {
      return false;
    }
    LazyParameterBound b = (LazyParameterBound) obj;
    return this.boundType.equals(b.boundType);
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(boundType);
  }

  @Override
  public String toString(@PolyDet LazyParameterBound this) {
    @SuppressWarnings("determinism") // all implementation toString methods deterministic
    @PolyDet String tmp = boundType.toString();
    return tmp;
  }

  @Override
  public ParameterBound substitute(@Det LazyParameterBound this, @Det Substitution substitution) {
    if (substitution.isEmpty()) {
      return this;
    }
    if (boundType instanceof java.lang.reflect.TypeVariable) {
      @Det ReferenceType referenceType = substitution.get(boundType);
      if (referenceType != null) {
        if (referenceType.isVariable()) {
          return new LazyReferenceBound(referenceType);
        }
        return new EagerReferenceBound(referenceType);
      }
      return this;
    }

    if (boundType instanceof java.lang.reflect.ParameterizedType) {
      boolean isLazy = false;
      List<TypeArgument> argumentList = new ArrayList<>();
      for (java.lang.reflect.Type parameter :
          ((ParameterizedType) boundType).getActualTypeArguments()) {
        java.lang.reflect.Type tmp = parameter;
        @Det TypeArgument typeArgument = substitute(tmp, substitution);
        if (typeArgument == null) {
          return this;
        }
        isLazy = isTypeVariable(tmp) && typeArgument.isVariable();
        argumentList.add(typeArgument);
      }
      @Det GenericClassType classType =
          GenericClassType.forClass((Class<?>) ((ParameterizedType) boundType).getRawType());
      @Det InstantiatedType instantiatedType = new InstantiatedType(classType, argumentList);
      if (isLazy) {
        return new LazyReferenceBound(instantiatedType);
      }
      return new EagerReferenceBound(instantiatedType);
    }

    throw new RandoopBug(
        "lazy parameter bounds should be either a type variable or parameterized type");
  }

  /**
   * Applies a substitution to a reflection type that occurs as an actual argument of a
   * parameterized type bound, to create a type argument to a {@link
   * randoop.types.ParameterizedType}.
   *
   * @param type the reflection type
   * @param substitution the type substitution
   * @return the type argument
   */
  private static TypeArgument substitute(
      java.lang.reflect.@Det Type type, @Det Substitution substitution) {
    if (type instanceof java.lang.reflect.TypeVariable) {
      @Det ReferenceType referenceType = substitution.get(type);
      if (referenceType != null) {
        return TypeArgument.forType(referenceType);
      }
      return null;
    }

    if (type instanceof java.lang.reflect.ParameterizedType) {
      List<TypeArgument> argumentList = new ArrayList<>();
      for (java.lang.reflect.Type parameter : ((ParameterizedType) type).getActualTypeArguments()) {
        @Det TypeArgument paramType = substitute(parameter, substitution);
        argumentList.add(paramType);
      }
      @Det GenericClassType classType =
          GenericClassType.forClass((Class<?>) ((ParameterizedType) type).getRawType());
      @Det InstantiatedType instantiatedType = new InstantiatedType(classType, argumentList);
      return TypeArgument.forType(instantiatedType);
    }

    if (type instanceof Class) {
      return TypeArgument.forType(ClassOrInterfaceType.forType(type));
    }

    if (type instanceof java.lang.reflect.WildcardType) {
      final java.lang.reflect.WildcardType wildcardType = (java.lang.reflect.WildcardType) type;
      if (wildcardType.getLowerBounds().length > 0) {
        assert wildcardType.getLowerBounds().length == 1
            : "a wildcard is defined by the JLS to only have one bound";
        java.lang.reflect.@Det Type lowerBound = wildcardType.getLowerBounds()[0];
        @Det ParameterBound bound;
        if (lowerBound instanceof java.lang.reflect.TypeVariable) {
          @Det ReferenceType boundType = substitution.get(lowerBound);
          if (boundType != null) {
            bound = ParameterBound.forType(boundType);
          } else {
            bound = new LazyParameterBound(lowerBound);
          }
        } else {
          bound =
              ParameterBound.forType(
                      new @OrderNonDet HashSet<java.lang.reflect.TypeVariable<?>>(), lowerBound)
                  .substitute(substitution);
        }

        return new WildcardArgument(new WildcardType(bound, false));
      }
      // a wildcard always has an upper bound
      assert wildcardType.getUpperBounds().length == 1
          : "a wildcard is defined by the JLS to only have one bound";
      @SuppressWarnings("determinism") // upon inspection, not a bug: forTypes returns @PolyDet("up") because
      // of the second argument, not the first. So as long as the second argument is @Det, the
      // result should be @Det, even if the first argument is @OrderNonDet.
      @Det ParameterBound bound =
          ParameterBound.forTypes(
              new HashSet<java.lang.reflect.TypeVariable<?>>(), wildcardType.getUpperBounds());
      bound = bound.substitute(substitution);
      return new WildcardArgument(new WildcardType(bound, true));
    }

    return null;
  }

  @Override
  public @Det ParameterBound applyCaptureConversion(@Det LazyParameterBound this) {
    throw new LazyBoundException();
    // assert false : "unable to do capture conversion on lazy bound " + this;
    // return this;
  }

  @Override
  public List<@PolyDet TypeVariable> getTypeParameters() {
    return getTypeParameters(boundType);
  }

  /**
   * Collects the type parameters from the given reflection {@code Type} object.
   *
   * @param type the {@code Type} reference
   * @return the list of type variables in the given type
   */
  @SuppressWarnings("determinism") // valid rule relaxation: no unintended aliasing, so addAll can take @PolyDet
  private static List<@PolyDet TypeVariable> getTypeParameters(java.lang.reflect.Type type) {
    @PolyDet List<@PolyDet TypeVariable> variableList = new @PolyDet ArrayList<>();
    if (type instanceof java.lang.reflect.TypeVariable) {
      variableList.add(TypeVariable.forType(type));
    } else if (type instanceof java.lang.reflect.ParameterizedType) {
      java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) type;
      for (java.lang.reflect.Type argType : pt.getActualTypeArguments()) {
        variableList.addAll(getTypeParameters(argType));
      }
    } else if (type instanceof java.lang.reflect.WildcardType) {
      java.lang.reflect.WildcardType wt = (java.lang.reflect.WildcardType) type;
      for (java.lang.reflect.Type boundType : wt.getUpperBounds()) {
        variableList.addAll(getTypeParameters(boundType));
      }
      for (java.lang.reflect.Type boundType : wt.getLowerBounds()) {
        variableList.addAll(getTypeParameters(boundType));
      }
    }
    return variableList;
  }

  @Override
  boolean hasWildcard() {
    return hasWildcard(boundType);
  }

  private static boolean hasWildcard(java.lang.reflect.Type type) {
    if (type instanceof java.lang.reflect.WildcardType) {
      return true;
    }
    if (type instanceof java.lang.reflect.TypeVariable) {
      for (java.lang.reflect.Type bound : ((java.lang.reflect.TypeVariable) type).getBounds()) {
        if (hasWildcard(bound)) {
          return true;
        }
      }
      return false;
    }
    if (type instanceof java.lang.reflect.ParameterizedType) {
      java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) type;
      for (java.lang.reflect.Type argType : pt.getActualTypeArguments()) {
        if (hasWildcard(argType)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isGeneric() {
    return true;
  }

  @Override
  public boolean isLowerBound(
      @Det LazyParameterBound this, @Det Type argType, @Det Substitution substitution) {
    ParameterBound b = this.substitute(substitution);
    if (b.equals(this)) {
      throw new IllegalArgumentException(
          "substitution " + substitution + " does not instantiate " + this);
    }
    return b.isLowerBound(argType, substitution);
  }

  @Override
  public boolean isObject() {
    return false;
  }

  @Override
  public boolean isSubtypeOf(@Det LazyParameterBound this, @Det ParameterBound boundType) {
    throw new LazyBoundException();
    // assert false : "LazyParameterBound.isSubtypeOf not implemented";
    // return false;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This generic type bound is satisfied by a concrete type if the concrete type formed by
   * applying the substitution to this generic bound is satisfied by the concrete type.
   */
  @Override
  public boolean isUpperBound(
      @Det LazyParameterBound this, @Det Type argType, @Det Substitution substitution) {
    ParameterBound b = this.substitute(substitution);
    if (b.equals(this)) {
      throw new IllegalArgumentException(
          "substitution " + substitution + " does not instantiate " + this);
    }
    return b.isUpperBound(argType, substitution);
  }

  @Override
  boolean isUpperBound(
      @Det LazyParameterBound this, @Det ParameterBound bound, @Det Substitution substitution) {
    throw new LazyBoundException();
    // assert false : " not quite sure what to do with lazy type bound";
    // return false;
  }

  @Override
  public boolean isVariable() {
    return boundType instanceof java.lang.reflect.TypeVariable;
  }

  /** There was an attempt to perform an operation, such as capture conversion, on a lazy bound. */
  static class LazyBoundException extends RuntimeException {
    private static final long serialVersionUID = 20190508;
  }
}
