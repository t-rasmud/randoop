package randoop.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.NoQualifierParameter;

/**
 * A MultiMap that supports checkpointing and restoring to a checkpoint (that is, undoing all
 * operations up to a checkpoint, also called a "mark").
 */
public class CheckpointingMultiMap<
        T1 extends @PolyDet("use") Object, T2 extends @PolyDet("use") Object>
    implements IMultiMap<T1, T2> {

  public static boolean verbose_log = false;

  private final @PolyDet Map<T1, @PolyDet Set<T2>> map;

  public final @PolyDet List<@PolyDet Integer> marks;

  @NoQualifierParameter(NonDet.class)
  private enum Ops {
    ADD,
    REMOVE
  }

  private final @PolyDet List<@PolyDet OpKeyVal> ops;

  private @PolyDet int steps;

  // A triple of an operation, a key, and a value
  private class OpKeyVal {
    final @PolyDet Ops op;
    final @PolyDet T1 key;
    final @PolyDet T2 val;

    OpKeyVal(final @PolyDet Ops op, final T1 key, final T2 val) {
      this.op = op;
      this.key = key;
      this.val = val;
    }
  }

  public CheckpointingMultiMap() {
    map = new @PolyDet LinkedHashMap<>();
    marks = new @PolyDet ArrayList<>();
    ops = new @PolyDet ArrayList<>();
    steps = 0;
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#add(T1, T2)
   */
  @Override
  public void add(T1 key, T2 value) {
    if (verbose_log) {
      Log.logPrintf("ADD %s -> %s%n", key, value);
    }
    add_bare(key, value);
    Object dummy = ops.add(new @PolyDet OpKeyVal(Ops.ADD, key, value));
    steps++;
  }

  private void add_bare(T1 key, T2 value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("args cannot be null.");
    }

    @PolyDet Set<T2> values = map.get(key);
    if (values == null) {
      values = new @PolyDet LinkedHashSet<>(1);
      map.put(key, values);
    }
    if (values.contains(value)) {
      throw new IllegalArgumentException("Mapping already present: " + key + " -> " + value);
    }
    values.add(value);
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#remove(T1, T2)
   */
  @Override
  public void remove(T1 key, T2 value) {
    if (verbose_log) {
      Log.logPrintf("REMOVE %s -> %s%n", key, value);
    }
    remove_bare(key, value);
    Object dummy = ops.add(new @PolyDet OpKeyVal(Ops.REMOVE, key, value));
    steps++;
  }

  private void remove_bare(T1 key, T2 value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("args cannot be null.");
    }

    Set<T2> values = map.get(key);
    if (values == null) {
      throw new IllegalArgumentException("Mapping not present: " + key + " -> " + value);
    }
    values.remove(value);

    // If no more mapping from key, remove key from map.
    if (values.isEmpty()) {
      map.remove(key);
    }
  }

  /** Checkpoint the state of the data structure, for use by {@link #undoToLastMark()}. */
  public void mark() {
    marks.add(steps);
    steps = 0;
  }

  /** Undo changes since the last call to {@link #mark()}. */
  public void undoToLastMark() {
    if (marks.isEmpty()) {
      throw new IllegalArgumentException("No marks.");
    }
    Log.logPrintf("marks: %s%n", marks);
    for (int i = 0; i < steps; i++) {
      undoLastOp();
    }
    @SuppressWarnings("determinism") // method receiver can't be @OrderNonDet so @PolyDet("up") is
    // the same as @PolyDet
    @PolyDet int tmp = marks.remove(marks.size() - 1);
    steps = tmp;
  }

  @SuppressWarnings("determinism") // https://github.com/t-rasmud/checker-framework/issues/143
  private void undoLastOp() {
    if (ops.isEmpty()) throw new IllegalStateException("ops empty.");
    @SuppressWarnings(
        "determinism") // method receiver can't be @OrderNonDet so @PolyDet("up") is the same as
    // @PolyDet
    @PolyDet OpKeyVal last = ops.remove(ops.size() - 1);
    @PolyDet Ops op = last.op;
    T1 key = last.key;
    T2 val = last.val;

    if (op == Ops.ADD) {
      // Remove the mapping.
      Log.logPrintf("REMOVE %s%n", key + " ->" + val);
      remove_bare(key, val);
    } else if (op == Ops.REMOVE) {
      // Add the mapping.
      Log.logPrintf("ADD %s -> %s%n", key, val);
      add_bare(key, val);
    } else {
      // Really, we should never get here.
      throw new IllegalStateException("Unhandled op: " + op);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#getValues(T1)
   */
  @Override
  public Set<T2> getValues(T1 key) {
    if (key == null) throw new IllegalArgumentException("arg cannot be null.");
    Set<T2> values = map.get(key);
    if (values == null) {
      return (@PolyDet Set<T2>) Collections.emptySet();
    }
    return values;
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#keySet()
   */
  @Override
  public Set<T1> keySet() {
    return map.keySet();
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#size()
   */
  @Override
  public int size() {
    return map.size();
  }

  /*
   * (non-Javadoc)
   *
   * @see randoop.util.IMultiMap#toString()
   */
  @Override
  public @NonDet String toString() {
    return map.toString();
  }
}
