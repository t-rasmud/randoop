package randoop.types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** An abstract class representing type variables. */
public abstract class TypeVariable extends ParameterType {

  /**
   * Creates a type variable with {@link NullReferenceType} as the lower bound, and the {@code
   * Object} type as upper bound.
   */
  TypeVariable() {
    super();
  }

  /**
   * Creates a type variable with the given type bounds. Assumes the bounds are consistent and does
   * not check for the subtype relationship.
   *
   * @param lowerBound the lower type bound on this variable
   * @param upperBound the upper type bound on this variable
   */
  TypeVariable(ParameterBound lowerBound, ParameterBound upperBound) {
    super(lowerBound, upperBound);
  }

  /**
   * Creates a {@code TypeVariable} object for a given {@code java.lang.reflect.Type} reference,
   * which must be a {@code java.lang.reflect.TypeVariable}.
   *
   * @param type the type reference
   * @return the {@code TypeVariable} for the given type
   */
  public static TypeVariable forType(java.lang.reflect.Type type) {
    if (!(type instanceof java.lang.reflect.TypeVariable<?>)) {
      throw new IllegalArgumentException("type must be a type variable, got " + type);
    }
    java.lang.reflect.TypeVariable<?> v = (java.lang.reflect.TypeVariable) type;
    @PolyDet("upDet") Set<java.lang.reflect.@PolyDet TypeVariable<?>> variableSet = new @PolyDet("upDet") HashSet<>();
    variableSet.add(v);
    @SuppressWarnings("determinism") // @PolyDet("up") is the same as @PolyDet
    @PolyDet TypeVariable tmp =
        new ExplicitTypeVariable(v, ParameterBound.forTypes(variableSet, v.getBounds()));
    return tmp;
  }

  @Override
  public ReferenceType substitute(@Det TypeVariable this, @Det Substitution substitution) {
    @Det ReferenceType type = substitution.get(this);
    if (type != null) {
      return type;
    }
    return this;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns false, since an uninstantiated type variable may not be assigned to.
   */
  @Override
  public boolean isAssignableFrom(@Det TypeVariable this, @Det Type sourceType) {
    return false;
  }

  @Override
  public boolean isInstantiationOf(@Det TypeVariable this, @Det ReferenceType otherType) {
    if (super.isInstantiationOf(otherType)) {
      return true;
    }

    if (otherType.isVariable()) {
      @Det TypeVariable otherVariable = (TypeVariable) otherType;
      @Det Substitution substitution = getSubstitution(otherVariable, this);
      boolean lowerboundOk =
          otherVariable.getLowerTypeBound().isLowerBound(getLowerTypeBound(), substitution);
      boolean upperboundOk =
          otherVariable.getUpperTypeBound().isUpperBound(getUpperTypeBound(), substitution);
      return lowerboundOk && upperboundOk;
    }

    return false;
  }

  @Override
  public boolean isSubtypeOf(@Det TypeVariable this, @Det Type otherType) {
    if (super.isSubtypeOf(otherType)) {
      return true;
    }
    if (otherType.isReferenceType()) {
      @Det Substitution substitution = getSubstitution(this, (ReferenceType) otherType);
      return this.getUpperTypeBound().isLowerBound(otherType, substitution);
    }
    return false;
  }

  /**
   * Creates a substitution of the given {@link ReferenceType} for the {@link TypeVariable}.
   *
   * @param variable the variable
   * @param otherType the replacement type
   * @return a substitution that replaces {@code variable} with {@code otherType}
   */
  private static Substitution getSubstitution(TypeVariable variable, ReferenceType otherType) {
    return new Substitution(variable, otherType);
  }

  @Override
  public boolean isVariable() {
    return true;
  }

  /**
   * Indicates whether this {@link TypeVariable} can be instantiated by the {@link ReferenceType}.
   * Does not require that all bounds of this variable be instantiated.
   *
   * @param otherType the possibly instantiating type, not a variable
   * @return true if the given type can instantiate this variable, false otherwise
   */
  boolean canBeInstantiatedBy(@Det TypeVariable this, @Det ReferenceType otherType) {
    @Det Substitution substitution;
    if (getLowerTypeBound().isVariable()) {
      substitution = getSubstitution(this, otherType);
      ParameterBound boundType = getLowerTypeBound().substitute(substitution);
      @Det TypeVariable checkType = (TypeVariable) ((ReferenceBound) boundType).getBoundType();
      if (!checkType.canBeInstantiatedBy(otherType)) {
        return false;
      }
    } else {
      substitution = getSubstitution(this, otherType);
      if (!getLowerTypeBound().isLowerBound(otherType, substitution)) {
        return false;
      }
    }
    if (getUpperTypeBound().isVariable()) {
      substitution = getSubstitution(this, otherType);
      ParameterBound boundType = getUpperTypeBound().substitute(substitution);
      @Det TypeVariable checkType = (TypeVariable) ((ReferenceBound) boundType).getBoundType();
      if (!checkType.canBeInstantiatedBy(otherType)) {
        return false;
      }
    } else {
      substitution = getSubstitution(this, otherType);
      if (!getUpperTypeBound().isUpperBound(otherType, substitution)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the type parameters in this type, which is this variable.
   *
   * @return this variable
   */
  @Override
  public List<@PolyDet TypeVariable> getTypeParameters() {
    @PolyDet Set<@PolyDet TypeVariable> parameters = new LinkedHashSet<>(super.getTypeParameters());
    parameters.add(this);
    return new ArrayList<>(parameters);
  }

  public abstract TypeVariable createCopyWithBounds(
      @Det TypeVariable this, @Det ParameterBound lowerBound, @Det ParameterBound upperBound);

  @Override
  public Type getRawtype(@Det TypeVariable this) {
    return JavaTypes.OBJECT_TYPE;
  }
}
