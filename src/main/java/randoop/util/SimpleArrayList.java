package randoop.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public @PolyDet class SimpleArrayList<E extends @PolyDet Object> extends @PolyDet ArrayList<E>
    implements SimpleList<E>, Serializable {

  private static final long serialVersionUID = 20180317;

  public SimpleArrayList(@PolyDet Collection<? extends E> c) {
    super(c);
  }

  public SimpleArrayList() {
    super();
  }

  public SimpleArrayList(int initialCapacity) {
    super(initialCapacity);
  }

  @Override
  // Return the entire list.
  public SimpleList<E> getSublist(int index) {
    return this;
  }

  @Override
  public List<E> toJDKList() {
    return new ArrayList<>(this);
  }
}
