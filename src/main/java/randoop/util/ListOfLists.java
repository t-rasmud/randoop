package randoop.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;
import randoop.main.RandoopBug;

/**
 * Given a list of lists, defines methods that can access all the elements as if they were part of a
 * single list, without actually merging the lists.
 *
 * <p>This class is used for performance reasons. We want the ability to select elements collected
 * across several lists, but we observed that creating a brand new list (i.e. via a sequence of
 * List.addAll(..) operations can be very expensive, because it happened in a hot spot (method
 * SequenceCollection.getSequencesThatYield).
 */
@HasQualifierParameter(NonDet.class)
public class ListOfLists<T extends @PolyDet Object> implements SimpleList<T>, Serializable {

  private static final long serialVersionUID = -3307714585442970263L;

  public final @PolyDet List<@PolyDet SimpleList<T>> lists;

  /** The i-th value is the number of elements in the sublists up to the i-th one, inclusive. */
  private @PolyDet int @PolyDet [] cumulativeSize;

  /** The size of this collection. */
  private @PolyDet int totalelements;

  @SuppressWarnings({
    "unchecked"
  }) // heap pollution warning
  public @PolyDet("up") ListOfLists(@PolyDet SimpleList<T> @PolyDet ... lists) {
    this.lists = new @PolyDet("up") ArrayList<>(lists.length);
    for (@PolyDet("up") SimpleList<T> sl : lists) {
      this.lists.add(sl);
    }
    this.cumulativeSize = new int @PolyDet("up") [lists.length];
    this.totalelements = 0;
    for (int i = 0; i < lists.length; i++) {
      @PolyDet("up") SimpleList<T> l = lists[i];
      if (l == null) {
        throw new IllegalArgumentException("All lists should be non-null");
      }
      this.totalelements += l.size();
      this.cumulativeSize[i] = this.totalelements;
    }
  }

  @SuppressWarnings("determinism") // iterating over @OrderNonDet collection to create another
  public @PolyDet("up") ListOfLists(@PolyDet List<@PolyDet SimpleList<T>> lists) {
    if (lists == null) throw new IllegalArgumentException("param cannot be null");
    this.lists = lists;
    this.cumulativeSize = new int @PolyDet("up") [lists.size()];
    this.totalelements = 0;
    for (int i = 0; i < lists.size(); i++) {
      @PolyDet("up") SimpleList<T> l = lists.get(i);
      if (l == null) {
        throw new IllegalArgumentException("All lists should be non-null");
      }
      this.totalelements += l.size();
      this.cumulativeSize[i] = this.totalelements;
    }
  }

  @Override
  public int size() {
    return this.totalelements;
  }

  @Override
  public boolean isEmpty() {
    return this.totalelements == 0;
  }

  @Override
  public @PolyDet("up") T get(int index) {
    if (index < 0 || index > this.totalelements - 1) {
      throw new IllegalArgumentException("index must be between 0 and size()-1");
    }
    int previousListSize = 0;
    for (int i = 0; i < this.cumulativeSize.length; i++) {
      if (index < this.cumulativeSize[i]) {
        return this.lists.get(i).get(index - previousListSize);
      }
      previousListSize = this.cumulativeSize[i];
    }
    throw new RandoopBug("Indexing error in ListOfLists");
  }

  @Override
  public @PolyDet SimpleList<T> getSublist(int index) {
    if (index < 0 || index > this.totalelements - 1) {
      throw new IllegalArgumentException("index must be between 0 and size()-1");
    }
    int previousListSize = 0;
    for (int i = 0; i < this.cumulativeSize.length; i++) {
      if (index < this.cumulativeSize[i]) {
        // Recurse.
        @PolyDet SimpleList<T> tmp = lists.get(i).getSublist(index - previousListSize);
        return tmp;
      }
      previousListSize = cumulativeSize[i];
    }
    throw new RandoopBug("indexing error in ListOfLists");
  }

  @Override
  public List<T> toJDKList() {
    @PolyDet List<T> result = new @PolyDet ArrayList<>();
    for (@PolyDet("up") SimpleList<T> l : lists) {
      @SuppressWarnings(
          "determinism") // addAll requires @PolyDet("down") but not in the case of just making a
      // copy
      boolean dummy = result.addAll(l.toJDKList());
    }
    return result;
  }

  @Override
  public @PolyDet String toString() {
    @SuppressWarnings(
        "determinism") // all concrete implementation of type of a deterministic toString
    @PolyDet String tmp = toJDKList().toString();
    return tmp;
  }
}
