package randoop.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.plumelib.util.UtilPlume;

// TODO: why is this class needed?  Why is "Type[]" not adequate?
// (As an initial step toward that, I could make the internal representation be "Type[]".)
/**
 * {@code TypeTuple} represents an immutable ordered tuple of {@link Type} objects. Type tuples are
 * primarily used to represent the input types of operations.
 */
public class TypeTuple implements Iterable<@PolyDet Type>, Comparable<@PolyDet TypeTuple> {

  /** The sequence of types in this type tuple. */
  private final ArrayList<@PolyDet Type> list;

  /**
   * Creates a type tuple from the list of types, preserving the order.
   *
   * @param list the list of types
   */
  public TypeTuple(@PolyDet List<@PolyDet Type> list) {
    this.list = new @PolyDet ArrayList<>(list);
  }

  /** Creates an empty type tuple. */
  public TypeTuple() {
    this(new ArrayList<Type>());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TypeTuple)) {
      return false;
    }
    TypeTuple tuple = (TypeTuple) obj;
    @PolyDet boolean tmp = list.equals(tuple.list);
    return tmp;
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(list);
  }

  @Override
  public String toString() {
    @PolyDet String tmp = "(" + UtilPlume.join(list, ", ") + ")";
    return tmp;
  }

  /**
   * Applies a substitution to a type tuple, replacing any occurrences of type variables. Resulting
   * tuple may only be partially instantiated. Returns a new TypeTuple; the receiver is not
   * side-effected.
   *
   * @param substitution the substitution
   * @return a new type tuple resulting from applying the given substitution to this tuple
   */
  public @Det TypeTuple substitute(@Det TypeTuple this, @Det Substitution substitution) {
    List<Type> typeList = new ArrayList<>();
    for (Type type : this.list) {
      Type tmp = type;
      Type newType = tmp.substitute(substitution);
      if (newType != null) {
        typeList.add(newType);
      } else {
        typeList.add(tmp);
      }
    }
    return new TypeTuple(typeList);
  }

  /**
   * Applies a capture conversion to each component of this type type tuple. Returns a new
   * TypeTuple; the receiver is not side-effected.
   *
   * @return a new type tuple after performing a capture conversion
   */
  public @Det TypeTuple applyCaptureConversion(@Det TypeTuple this) {
    List<Type> typeList = new ArrayList<>();
    for (Type type : this.list) {
      Type tmp = type;
      typeList.add(tmp.applyCaptureConversion());
    }
    return new TypeTuple(typeList);
  }

  /**
   * Return the ith component type of this tuple.
   *
   * @param i the component index
   * @return the component type at the position
   */
  public Type get(int i) {
    @SuppressWarnings(
        "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
    // @PolyDet
    @PolyDet Type tmp = list.get(i);
    return tmp;
  }

  /**
   * Returns the type parameters that occur in any component of this type tuple.
   *
   * @return the list of type parameters for this type tuple
   */
  public List<@PolyDet TypeVariable> getTypeParameters() {
    @PolyDet Set<@PolyDet TypeVariable> paramSet = new @PolyDet LinkedHashSet<>();
    for (Type type : this.list) {
      @SuppressWarnings(
          "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
      // @PolyDet
      @PolyDet Type tmp = type;
      if (tmp.isReferenceType()) {
        boolean ignore = paramSet.addAll(((ReferenceType) tmp).getTypeParameters());
      }
    }
    return new ArrayList<>(paramSet);
  }

  /**
   * Indicates whether any of the types in this type tuple contains a wildcard.
   *
   * @return true if there is at least one wildcard occurrence
   */
  @SuppressWarnings("determinism") // process is order insensitive, but can't be verified
  public boolean hasWildcard() {
    for (Type type : list) {
      @PolyDet Type tmp = (ParameterizedType) type;
      if (type.isParameterized() && tmp.hasWildcard()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Indicates whether the tuple is empty.
   *
   * @return true if the tuple has no components, false otherwise
   */
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /**
   * Return the number of components of the tuple.
   *
   * @return the number of components of this tuple
   */
  public int size() {
    return list.size();
  }

  /**
   * Indicates whether the tuple has any generic components.
   *
   * @return true if any component of tuple is generic, false if none are
   */
  public boolean isGeneric() {
    for (Type type : list) {
      if (type.isGeneric()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Iterator<@PolyDet Type> iterator() {
    return new TypeIterator(list.iterator());
  }

  @Override
  public int compareTo(TypeTuple tuple) {
    if (this.size() < tuple.size()) {
      return -1;
    }
    if (this.size() > tuple.size()) {
      return 1;
    }
    int result = 0;
    for (int i = 0; i < this.size() && result == 0; i++) {
      @SuppressWarnings(
          "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
      // @PolyDet
      @PolyDet int tmp = list.get(i).compareTo(tuple.list.get(i));
      result = tmp;
    }
    return result;
  }

  @SuppressWarnings("determinism") // https://github.com/t-rasmud/checker-framework/issues/134
  private static class TypeIterator implements Iterator<@PolyDet Type> {

    private Iterator<@PolyDet Type> iterator;

    public TypeIterator(@PolyDet Iterator<@PolyDet Type> iterator) {
      this.iterator = iterator;
    }

    @Override
    public @PolyDet("down") boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public @PolyDet("up") Type next() {
      return iterator.next();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
