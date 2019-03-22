package randoop.generation;

import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import randoop.sequence.Sequence;

/**
 * Return type for ForwardGenerator's private selectInputs method, which is responsible for
 * selecting a set of component sequences to be concatenated into a longer sequence.
 */
class InputsAndSuccessFlag {

  /**
   * True if private method {@code ForwardGenerator.selectInputs(TypedOperation operation)} was able
   * to find component sequences for all the input types required by the given statement.
   */
  public boolean success;

  public List<Sequence> sequences;
  public List<Integer> indices;

  public InputsAndSuccessFlag(
      @Det boolean success, @Det List<Sequence> sequences, @Det List<Integer> vars) {
    this.success = success;
    this.sequences = sequences;
    this.indices = vars;
  }
}
