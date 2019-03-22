package randoop.generation;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.sequence.Sequence;
import randoop.util.Randomness;
import randoop.util.SimpleList;

/**
 * Select input sequences, favoring shorter sequences. This makes Randoop produce smaller JUnit
 * tests.
 */
public class SmallTestsSequenceSelection implements InputSequenceSelector {

  /** Map from a sequence to its weight. */
  private final @OrderNonDet Map<Sequence, Double> weightMap = new HashMap<>();

  /**
   * Pick a sequence from the candidate list using a weighting that favors shorter sequences.
   *
   * @param candidates sequences to choose from
   * @return the chosen sequence
   */
  @Override
  public Sequence selectInputSequence(
      @Det SmallTestsSequenceSelection this, @Det SimpleList<Sequence> candidates) {
    double totalWeight = updateWeightMapForCandidates(candidates);
    return Randomness.randomMemberWeighted(candidates, weightMap, totalWeight);
  }

  /**
   * Update the weight for any sequence not currently in the map, and compute the total weight.
   *
   * @param candidates the elements to compute a weight for
   * @return the total weight of all the candidates
   */
  private @PolyDet("up") double updateWeightMapForCandidates(SimpleList<Sequence> candidates) {

    double totalWeight = 0.0;
    for (int i = 0; i < candidates.size(); i++) {
      @Det Sequence candidate = candidates.get(i);
      @Det Double weight = weightMap.get(candidate);
      if (weight == null) {
        weight = 1 / (double) candidate.size();
      }
      totalWeight += weight;
      weightMap.put(candidate, weight);
    }
    return totalWeight;
  }
}
