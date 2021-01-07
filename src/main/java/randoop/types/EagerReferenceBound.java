package randoop.types;

import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.types.LazyParameterBound.LazyBoundException;

/**
 * Represents a bound on a type variable where the bound is a {@link ReferenceType} that can be used
 * directly. Contrast with {@link LazyReferenceBound}.
 */
class EagerReferenceBound extends ReferenceBound {

  /**
   * Creates a bound for the given reference type.
   *
   * @param boundType the reference boundType
   */
  EagerReferenceBound(@Det ReferenceType boundType) {
    super(boundType);
  }

  @Override
  public @Det EagerReferenceBound substitute(
      @Det EagerReferenceBound this, @Det Substitution substitution) {
    @Det ReferenceType referenceType = getBoundType().substitute(substitution);
    if (referenceType.equals(getBoundType())) {
      return this;
    }
    return new EagerReferenceBound(referenceType);
  }

  @Override
  public @Det EagerReferenceBound applyCaptureConversion(@Det EagerReferenceBound this) {
    @Det ReferenceType referenceType = getBoundType().applyCaptureConversion();
    if (referenceType.equals(getBoundType())) {
      return this;
    }
    return new EagerReferenceBound(referenceType);
  }

  @Override
  public List<@PolyDet TypeVariable> getTypeParameters() {
    return getBoundType().getTypeParameters();
  }

  @Override
  public boolean isLowerBound(
      @Det EagerReferenceBound this, @Det Type argType, @Det Substitution subst) {
    @Det ReferenceType boundType = this.getBoundType().substitute(subst);
    if (boundType.equals(JavaTypes.NULL_TYPE)) {
      return true;
    }
    if (boundType.isVariable()) {
      return ((TypeVariable) boundType).getLowerTypeBound().isLowerBound(argType, subst);
    }
    if (argType.isParameterized()) {
      if (!(boundType instanceof ClassOrInterfaceType)) {
        return false;
      }
      @Det InstantiatedType argClassType = (InstantiatedType) argType.applyCaptureConversion();
      @Det InstantiatedType boundSuperType =
          ((ClassOrInterfaceType) boundType)
              .getMatchingSupertype(argClassType.getGenericClassType());
      if (boundSuperType == null) {
        return false;
      }
      boundSuperType = boundSuperType.applyCaptureConversion();
      return boundSuperType.isInstantiationOf(argClassType);
    }
    return boundType.isSubtypeOf(argType);
  }

  @Override
  boolean isLowerBound(
      @Det EagerReferenceBound this, @Det ParameterBound bound, @Det Substitution substitution) {
    assert bound instanceof EagerReferenceBound : "only handling reference bounds";
    return isLowerBound(((EagerReferenceBound) bound).getBoundType(), substitution);
  }

  @Override
  public boolean isSubtypeOf(@Det EagerReferenceBound this, @Det ParameterBound bound) {
    if (bound instanceof EagerReferenceBound) {
      return this.getBoundType().isSubtypeOf(((EagerReferenceBound) bound).getBoundType());
    }
    assert false : "not handling EagerReferenceBound subtype of other bound type";
    return false;
  }

  @Override
  public boolean isUpperBound(
      @Det EagerReferenceBound this, @Det Type argType, @Det Substitution subst) {
    @Det ReferenceType boundType = this.getBoundType().substitute(subst);
    if (boundType.equals(JavaTypes.OBJECT_TYPE)) {
      return true;
    }
    if (boundType.isVariable()) {
      return ((TypeVariable) boundType).getUpperTypeBound().isUpperBound(argType, subst);
    }
    if (boundType.isParameterized()) {
      if (!(argType instanceof ClassOrInterfaceType)) {
        return false;
      }
      @Det InstantiatedType boundClassType;
      try {
        boundClassType = (InstantiatedType) boundType.applyCaptureConversion();
      } catch (LazyBoundException e) {
        // Capture conversion does not (currently?) work for a lazy bound.
        return false;
      }
      @Det InstantiatedType argSuperType =
          ((ClassOrInterfaceType) argType)
              .getMatchingSupertype(boundClassType.getGenericClassType());
      if (argSuperType == null) {
        return false;
      }
      argSuperType = argSuperType.applyCaptureConversion();
      return argSuperType.isInstantiationOf(boundClassType);
    }
    return argType.isSubtypeOf(boundType);
  }

  @Override
  boolean isUpperBound(
      @Det EagerReferenceBound this, @Det ParameterBound bound, @Det Substitution substitution) {
    return isUpperBound(getBoundType(), substitution);
  }
}
