package randoop.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public final class OneMoreElementList<T extends @PolyDet Object>
    implements SimpleList<T>, Serializable {

  private static final long serialVersionUID = 1332963552183905833L;

  public final T lastElement;
  public final SimpleList<T> list;
  public final int size;

  public OneMoreElementList(@PolyDet SimpleList<T> list, T extraElement) {
    this.list = list;
    this.lastElement = extraElement;
    this.size = list.size() + 1;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public @PolyDet("up") T get(int index) {
    if (index < size - 1) {
      return list.get(index);
    }
    if (index == size - 1) {
      return lastElement;
    }
    throw new IndexOutOfBoundsException("No such element: " + index);
  }

  @Override
  public SimpleList<T> getSublist(int index) {
    if (index == size - 1) { // is lastElement
      return this;
    }
    // Not the last element, so recurse.
    if (index < size - 1) {
      return list.getSublist(index);
    }
    throw new IndexOutOfBoundsException("No such index: " + index);
  }

  @Override
  public List<T> toJDKList() {
    @PolyDet List<T> result = new @PolyDet ArrayList<>();
    boolean dummy = result.addAll(list.toJDKList());
    result.add(lastElement);
    return result;
  }

  @Override
  public @PolyDet String toString() {
    @SuppressWarnings("determinism") // all implementation toString methods deterministic
    @PolyDet String tmp = toJDKList().toString();
    return tmp;
  }
}
