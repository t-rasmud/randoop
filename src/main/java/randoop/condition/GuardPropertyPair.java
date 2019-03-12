package randoop.condition;

import org.checkerframework.checker.determinism.qual.Det;

/**
 * Represents a pair of an executable guard and an executable property.
 *
 * <p>Corresponds to a {@link randoop.condition.specification.Postcondition}.
 */
public class GuardPropertyPair {

  /** The expression that must hold before the operation is called. */
  final ExecutableBooleanExpression guard;

  /**
   * The expression that must be true after the operation is called when the {@link #guard} is true.
   */
  final ExecutableBooleanExpression property;

  /**
   * Creates a {@link GuardPropertyPair} object for the guard and property expressions.
   *
   * @param guard the {@link ExecutableBooleanExpression} to be evaluated before the operation is
   *     called
   * @param property the {@link ExecutableBooleanExpression} to be evaluated after the operation is
   *     called
   */
  GuardPropertyPair(
      @Det ExecutableBooleanExpression guard, @Det ExecutableBooleanExpression property) {
    this.guard = guard;
    this.property = property;
  }

  @Override
  public String toString() {
    return String.format("GuardPropertyPair{guard=%s, property=%s)", guard, property);
  }
}
