package randoop.sequence;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.ExecutionOutcome;
import randoop.NotExecuted;

/**
 * Represents the unfolding execution of a sequence.
 *
 * <p>This is essentially a wrapper around {@code List<ExecutionOutcome>}. Stores information in a
 * list of ExecutionOutcome objects, one for each statement in the sequence.
 */
public final class Execution {

  // The execution outcome of each statement.
  final @PolyDet List<@PolyDet ExecutionOutcome> outcomes;

  private Set<@PolyDet Class<?>> coveredClasses;

  /**
   * Create an Execution to store the execution results of the given sequence. The list of outcomes
   * is initialized to NotExecuted for every statement.
   *
   * @param owner the executed sequence
   */
  public Execution(Sequence owner) {
    this.outcomes = new @PolyDet ArrayList<>(owner.size());
    for (int i = 0; i < owner.size(); i++) {
      outcomes.add(NotExecuted.create());
    }
    this.coveredClasses = new @PolyDet LinkedHashSet<>();
  }

  /**
   * The size of the list.
   *
   * @return the size of the list
   */
  public int size() {
    return outcomes.size();
  }

  /**
   * Get the outcome in the i-th slot.
   *
   * @param i the statement position
   * @return the outcome of the ith statement
   */
  public ExecutionOutcome get(int i) {
    @SuppressWarnings(
        "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
    // @PolyDet
    @PolyDet ExecutionOutcome tmp = outcomes.get(i);
    return tmp;
  }

  void addCoveredClass(Class<?> c) {
    coveredClasses.add(c);
  }

  Set<@PolyDet Class<?>> getCoveredClasses() {
    return coveredClasses;
  }
}
