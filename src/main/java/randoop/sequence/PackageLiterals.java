package randoop.sequence;

import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;

/**
 * For a given package P, PackageLiterals maps P (if present) to a collection of literals
 * (represented as single-element sequences) that can be used as inputs to classes in the given
 * package.
 */
public class PackageLiterals extends MappedSequences<@Det Package> {

  @Override
  public void addSequence(@Det PackageLiterals this, @Det Package key, @Det Sequence seq) {
    if (seq == null) throw new IllegalArgumentException("seq is null");
    if (!seq.isNonreceiver()) {
      throw new IllegalArgumentException("seq is not a primitive sequence");
    }
    super.addSequence(key, seq);
  }
}
