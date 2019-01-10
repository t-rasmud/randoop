package randoop.util;

import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;

/**
 * A Set that supports settingcheckpoints (also called "marks") and restoring the data structure's
 * state to them.
 */
public class CheckpointingSet<T extends @Det Object> implements ISimpleSet<T> {

  public final CheckpointingMultiMap<T, Boolean> map;

  public CheckpointingSet() {
    this.map = new CheckpointingMultiMap<>();
  }

  @Override
  public void add(@Det CheckpointingSet<T> this, T elt) {
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
  public void remove(@Det CheckpointingSet<T> this, T elt) {
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
    return map.keySet().toString();
  }
}
