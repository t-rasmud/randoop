package randoop.util;

import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;

/**
 * A Set that supports settingcheckpoints (also called "marks") and restoring the data structure's
 * state to them.
 */
public class CheckpointingSet<T extends @PolyDet Object> implements ISimpleSet<T> {

  public final @PolyDet CheckpointingMultiMap<T, @PolyDet Boolean> map;

  public CheckpointingSet() {
    this.map = new @PolyDet CheckpointingMultiMap<>();
  }

  @Override
  public void add(T elt) {
    if (elt == null) throw new IllegalArgumentException("arg cannot be null.");
    if (contains(elt)) throw new IllegalArgumentException("set already contains elt " + elt);
    map.add(elt, true);
  }

  @Override
  public boolean contains(T elt) {
    if (elt == null) throw new IllegalArgumentException("arg cannot be null.");
    return map.keySet().contains(elt);
  }

  @Override
  public Set<T> getElements() {
    return map.keySet();
  }

  @Override
  public void remove(T elt) {
    if (elt == null) {
      throw new IllegalArgumentException("arg cannot be null.");
    }

    if (!contains(elt)) {
      throw new IllegalArgumentException("set does not contain elt " + elt);
    }

    map.remove(elt, true);
  }

  @Override
  public int size() {
    return map.size();
  }

  /** Checkpoint the state of the data structure, for use by {@link #undoToLastMark()}. */
  public void mark() {
    map.mark();
  }

  /** Undo changes since the last call to {@link #mark()}. */
  public void undoToLastMark(@Det CheckpointingSet<T> this) {
    map.undoToLastMark();
  }

  @Override
  public String toString() {
    @SuppressWarnings(
        "determinism") // all concrete implementation of this interface have a deterministic
                       // toString
    @PolyDet String tmp = map.keySet().toString();
    return tmp;
  }
}
