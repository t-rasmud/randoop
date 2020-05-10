package randoop.generation;

import org.checkerframework.checker.determinism.qual.Det;
import randoop.sequence.Sequence;
import randoop.util.Randomness;
import randoop.util.SimpleList;

/** Select sequences uniformly at random. */
public class UniformRandomSequenceSelection implements InputSequenceSelector {

  /**
   * Pick randomly with uniform probability, a sequence from the candidate list.
   *
   * @param candidates sequences to choose from
   * @return the chosen sequence
   */
  @Override
  public Sequence selectInputSequence(
      @Det UniformRandomSequenceSelection this, @Det SimpleList<@Det Sequence> candidates) {
    return Randomness.randomMember(candidates);
  }
}
