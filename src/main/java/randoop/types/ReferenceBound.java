package randoop.types;

import java.util.Objects;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** Represents a bound on a type variable where the bound is a {@link ReferenceType}. */
public abstract class ReferenceBound extends ParameterBound {

  /** The type for this bound. */
  private final ReferenceType boundType;

  /**
   * Creates a {@link ReferenceBound} with the given bound type.
   *
   * @param boundType the {@link ReferenceType} of this bound
   */
  ReferenceBound(@PolyDet ReferenceType boundType) {
    this.boundType = boundType;
  }

  /**
   * Returns the {@link ReferenceType} bound of this type.
   *
   * @return the type for this bound
   */
  public ReferenceType getBoundType() {
    return boundType;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ReferenceBound)) {
      return false;
    }
    @SuppressWarnings("determinism") // casting here doesn't change the determinism type
    ReferenceBound bound = (ReferenceBound) obj;
    return this.boundType.equals(bound.boundType);
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(boundType);
  }

  @Override
  public String toString() {
    return boundType.toString();
  }

  @Override
  public abstract @Det ReferenceBound substitute(
      @Det ReferenceBound this, @Det Substitution substitution);

  @Override
  public abstract @Det ReferenceBound applyCaptureConversion(@Det ReferenceBound this);

  @Override
  boolean hasWildcard() {
    return boundType.hasWildcard();
  }

  @Override
  public boolean isGeneric() {
    return boundType.isGeneric();
  }

  @Override
  public boolean isObject() {
    return boundType.isObject();
  }

  @Override
  public boolean isVariable() {
    return boundType.isVariable();
  }
}
