package randoop.types;

import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** Predicate to check the type of a substitution for a list of type variables. */
public class BoundsCheck {
  /** The type variables to check against. */
  private final List<@PolyDet TypeVariable> typeParameters;

  /**
   * Creates a {@link BoundsCheck} predicate for a given list of type parameters.
   *
   * @param typeParameters the list of of type parameters to be checked by the predicate
   */
  public BoundsCheck(@PolyDet List<@PolyDet TypeVariable> typeParameters) {
    this.typeParameters = typeParameters;
  }

  /**
   * Checks if each type argument is within the bounds of the corresponding type parameter, after
   * both arguments and parameters have been substituted.
   *
   * <p>Requires that the list of types be the same length as the parameters. Generally, the
   * substitution is constructed from the tuple instantiating the type parameters of this object,
   * but all that is required is that it instantiate any generic bounds of the the type parameters.
   *
   * @param tuple the list of instantiating types
   * @param substitution substitution for checking bounds
   * @return true if each argument's instantiation is within the bounds of the corresponding type
   *     parameter, after both have been substituted
   */
  public boolean test(
      @Det BoundsCheck this, @Det List<ReferenceType> tuple, @Det Substitution substitution) {
    for (int i = 0; i < tuple.size(); i++) {
      @Det TypeVariable paramType = typeParameters.get(i);
      @Det ReferenceType argType = tuple.get(i);
      if (!(paramType.getLowerTypeBound().isLowerBound(argType, substitution)
          && paramType.getUpperTypeBound().isUpperBound(argType, substitution))) {
        return false;
      }
    }
    return true;
  }
}
