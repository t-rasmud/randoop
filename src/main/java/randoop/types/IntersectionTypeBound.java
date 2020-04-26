package randoop.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.plumelib.util.UtilPlume;

/**
 * Represents an intersection type bound on a type parameter in a class, interface, method or
 * constructor (see <a
 * href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.4">JLS section 4.4</a>).
 * Alternatively, in capture conversion, it may also represent the greatest lower bound of two upper
 * bounds ( <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.1.10">JLS
 * section 5.1.10</a>).
 *
 * <p>Java requires that an intersection type bound consist of class and interface types, with at
 * most one class, and if there is a class it appears in the conjunction term first. This class
 * preserves the order of the types. In a capture conversion, if both types are classes, one must be
 * a subclass of the other.
 */
class IntersectionTypeBound extends ParameterBound {

  /** the list of type bounds for the intersection bound */
  private List<@PolyDet ParameterBound> boundList;

  /**
   * Create an intersection type bound from the list of type bounds.
   *
   * @param boundList the list of type bounds
   */
  IntersectionTypeBound(@PolyDet List<@PolyDet ParameterBound> boundList) {
    if (boundList == null) {
      throw new IllegalArgumentException("bounds list may not be null");
    }

    this.boundList = boundList;
  }

  // XXX could be relaxed: only require that the first argument be first, if it is a class (rest can
  // be reordered)
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IntersectionTypeBound)) {
      return false;
    }
    @SuppressWarnings("determinism") // casting here doesn't change the determinism type
    IntersectionTypeBound b = (IntersectionTypeBound) obj;
    @SuppressWarnings(
        "determinism") // method receiver can't be @OrderNonDet so @PolyDet("up") is the same as
    // @PolyDet
    @PolyDet boolean tmp = this.boundList.equals(b.boundList);
    return tmp;
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(boundList);
  }

  @Override
  public String toString() {
    @SuppressWarnings("determinism") // the annotation for this library method is wrong
    @PolyDet String tmp = UtilPlume.join(boundList, " & ");
    return tmp;
  }

  /**
   * {@inheritDoc}
   *
   * @return this bound with the substitution applied to all member bounds
   */
  @Override
  public @Det IntersectionTypeBound substitute(
      @Det IntersectionTypeBound this, @Det Substitution substitution) {
    List<ParameterBound> bounds = new ArrayList<>();
    for (ParameterBound bound : this.boundList) {
      bounds.add(bound.substitute(substitution));
    }
    return new IntersectionTypeBound(bounds);
  }

  /**
   * {@inheritDoc}
   *
   * @return an intersection bound with capture conversion applied to all member bounds
   */
  @Override
  public ParameterBound applyCaptureConversion(@Det IntersectionTypeBound this) {
    List<ParameterBound> convertedBoundList = new ArrayList<>();
    for (ParameterBound b : boundList) {
      convertedBoundList.add(b.applyCaptureConversion());
    }
    return new IntersectionTypeBound(convertedBoundList);
  }

  /**
   * {@inheritDoc}
   *
   * @return the list of type variables occurring in all of the type bounds of this intersection
   *     bound
   */
  @Override
  public List<@PolyDet TypeVariable> getTypeParameters() {
    @PolyDet List<@PolyDet TypeVariable> paramList = new @PolyDet ArrayList<>();
    for (ParameterBound b : boundList) {
      @SuppressWarnings("determinism") // no unintended aliasing, so addAll can take @PolyDet
      boolean ignore = paramList.addAll(b.getTypeParameters());
    }
    return paramList;
  }

  @Override
  boolean hasWildcard() {
    for (ParameterBound b : boundList) {
      if (b.hasWildcard()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isGeneric() {
    for (ParameterBound b : boundList) {
      if (b.isGeneric()) {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Specifically, this method checks that the argument type is a subtype of all of the member
   * bounds of this object.
   */
  @Override
  public boolean isLowerBound(
      @Det IntersectionTypeBound this, @Det Type otherType, @Det Substitution subst) {
    for (ParameterBound b : boundList) {
      if (!b.isLowerBound(otherType, subst)) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Determines whether all types in this bound are {@code Object}.
   */
  @Override
  public boolean isObject() {
    for (ParameterBound b : boundList) {
      if (!b.isObject()) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * <p>This method should never be tested for {@link IntersectionTypeBound}. Will fail if
   * assertions are enabled.
   *
   * @return false, always
   */
  @Override
  public boolean isSubtypeOf(@Det IntersectionTypeBound this, @Det ParameterBound boundType) {
    assert false : "intersection type bound isSubTypeOf not implemented";
    return false;
  }

  /**
   * {@inheritDoc}
   *
   * @return true if the argument type satisfies all of the bounds in this intersection type bound
   */
  @Override
  public boolean isUpperBound(
      @Det IntersectionTypeBound this, @Det Type argType, @Det Substitution subst) {
    for (ParameterBound b : boundList) {
      if (!b.isUpperBound(argType, subst)) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @return true if the argument bound has all of the member bounds of this object as an upper
   *     bound
   */
  @Override
  boolean isUpperBound(
      @Det IntersectionTypeBound this, @Det ParameterBound bound, @Det Substitution substitution) {
    for (ParameterBound b : boundList) {
      if (!b.isUpperBound(bound, substitution)) {
        return false;
      }
    }
    return true;
  }
}
