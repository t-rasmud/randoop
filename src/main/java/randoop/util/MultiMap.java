package randoop.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

/** Implements an IMultiMap with a java.util.LinkedHashMap. */
@HasQualifierParameter(NonDet.class)
public class MultiMap<T1 extends @PolyDet("use") Object, T2 extends @PolyDet("use") Object>
    implements IMultiMap<T1, T2> {

  private final @PolyDet Map<T1, @PolyDet Set<T2>> map;

  public MultiMap() {
    map = new @PolyDet LinkedHashMap<>();
  }

  public MultiMap(int initialCapacity) {
    map = new @PolyDet LinkedHashMap<>(initialCapacity);
  }

  public void put(T1 key, @PolyDet("use") Collection<? extends T2> values) {
    if (contains(key)) remove(key);
    map.put(key, new LinkedHashSet<T2>(values));
  }

  @SuppressWarnings("determinism") // iterating over @PolyDet collection to modify another
  public void addAll(@PolyDet("use") Map<? extends T1, ? extends T2> m) {
    for (T1 t1 : m.keySet()) {
      add(t1, m.get(t1));
    }
  }

  @SuppressWarnings("determinism") // iterating over @PolyDet collection to modify another
  public void addAll(T1 key, @PolyDet("use") Collection<? extends T2> values) {
    for (T2 t2 : values) {
      add(key, t2);
    }
  }

  @SuppressWarnings("determinism") // iterating over @PolyDet collection to modify another
  public void addAll(MultiMap<T1, T2> mmap) {
    for (Map.Entry<T1, @PolyDet Set<T2>> entry : mmap.map.entrySet()) {
      addAll(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void add(T1 key, T2 value) {
    Set<T2> values = map.get(key);
    if (values == null) {
      values = new @PolyDet LinkedHashSet<>(1);
      map.put(key, values);
    }
    values.add(value);
  }

  @Override
  public void remove(T1 key, T2 value) {
    Set<T2> values = map.get(key);
    if (values == null) {
      throw new IllegalStateException(
          "No values where found when trying to remove from multiset. Key: "
              + key
              + " Variable: "
              + value);
    }
    values.remove(value);
  }

  public void remove(T1 key) {
    Set<T2> values = map.get(key);
    if (values == null) {
      throw new IllegalStateException(
          "No values where found when trying to remove from multiset. Key: " + key);
    }
    map.remove(key);
  }

  @Override
  @SuppressWarnings("determinism") // Collections.emptySet doesn't type check but is clearly valid
  public Set<T2> getValues(T1 key) {
    Set<T2> values = map.get(key);
    if (values == null) {
      return Collections.emptySet();
    }
    return values;
  }

  @Override
  public Set<T1> keySet() {
    return map.keySet();
  }

  public boolean contains(T1 obj) {
    return map.containsKey(obj);
  }

  public void clear() {
    map.clear();
  }

  @Override
  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public @NonDet String toString() {
    return map.toString();
  }
}
