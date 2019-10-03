package randoop.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import randoop.main.RandoopBug;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

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

  public final List<@PolyDet SimpleList<T>> lists;

  /** The i-th value is the number of elements in the sublists up to the i-th one, inclusive. */
  private @PolyDet int[] cumulativeSize;

  private int totalelements;

  @SuppressWarnings({"varargs", "unchecked"}) // heap pollution warning
  public ListOfLists(SimpleList<T> @PolyDet ... lists) {
    this.lists = new @PolyDet ArrayList<>(lists.length);
    for (SimpleList<T> sl : lists) {
      this.lists.add(sl);
    }
    this.cumulativeSize = new int @PolyDet [lists.length];
    this.totalelements = 0;
    for (int i = 0; i < lists.length; i++) {
      SimpleList<T> l = lists[i];
      if (l == null) {
        throw new IllegalArgumentException("All lists should be non-null");
      }
      this.totalelements += l.size();
      this.cumulativeSize[i] = this.totalelements;
    }
  }

  public ListOfLists(@PolyDet List<@PolyDet SimpleList<T>> lists) {
    if (lists == null) throw new IllegalArgumentException("param cannot be null");
    this.lists = lists;
    this.cumulativeSize = new int @PolyDet [lists.size()];
    this.totalelements = 0;
    for (int i = 0; i < lists.size(); i++) {
      SimpleList<T> l = lists.get(i);
      if (l == null) {
        throw new IllegalArgumentException("All lists should be non-null");
      }
      @SuppressWarnings("determinism") // iterating over @PolyDet collection to modify another
      @PolyDet int tmp = l.size();
      this.totalelements += tmp;
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
  public @PolyDet("up") SimpleList<T> getSublist(int index) {
    if (index < 0 || index > this.totalelements - 1) {
      throw new IllegalArgumentException("index must be between 0 and size()-1");
    }
    int previousListSize = 0;
    for (int i = 0; i < this.cumulativeSize.length; i++) {
      if (index < this.cumulativeSize[i]) {
        // Recurse.
        return lists.get(i).getSublist(index - previousListSize);
      }
      previousListSize = cumulativeSize[i];
    }
    throw new RandoopBug("indexing error in ListOfLists");
  }

  @Override
  public List<T> toJDKList() {
    List<T> result = new @PolyDet ArrayList<>();
    for (SimpleList<T> l : lists) {
      result.addAll(l.toJDKList());
    }
    return result;
  }

  @Override
  public @NonDet String toString() {
    return toJDKList().toString();
  }
}
