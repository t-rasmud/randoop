package randoop.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class KeyToMultiSet<T1 extends @PolyDet Object, T2 extends @PolyDet Object> {

  private final @PolyDet Map<T1, @PolyDet MultiSet<T2>> map;

  public KeyToMultiSet() {
    map = new @PolyDet LinkedHashMap<>();
  }

  public void addAll(@PolyDet("use") Map<? extends T1, ? extends T2> m) {
    for (T1 t1 : m.keySet()) {
      add(t1, m.get(t1));
    }
  }

  public void addAll(T1 key, @PolyDet("use") Collection<? extends T2> values) {
    for (T2 t2 : values) {
      add(key, t2);
    }
  }

  public void add(T1 key, T2 value) {
    @PolyDet MultiSet<T2> values = map.get(key);
    if (values == null) {
      values = new @PolyDet MultiSet<>();
    }
    values.add(value);
    map.put(key, values);
  }

  public void remove(T1 key, T2 value) {
    @PolyDet MultiSet<T2> values = map.get(key);
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
    @PolyDet MultiSet<T2> values = map.get(key);
    if (values == null) {
      throw new IllegalStateException(
          "No values where found when trying to remove from multiset. Key: " + key);
    }
    map.remove(key);
  }

  public Set<T2> getVariables(T1 key) {
    @PolyDet MultiSet<T2> values = map.get(key);
    if (values == null) {
      @SuppressWarnings("determinism") // valid rule relaxation: need to treat @Det collection as @PolyDet
      @PolyDet Set<T2> tmp = Collections.emptySet();
    }
    return values.getElements();
  }

  public Set<T1> keySet() {
    return map.keySet();
  }

  public boolean contains(T1 obj) {
    return map.containsKey(obj);
  }

  // Removes all keys with an empty set
  @SuppressWarnings("determinism") // @PolyDet not instantiated correctly in type arguments
  public void clean(@Det KeyToMultiSet<T1, T2> this) {
    for (@Det Iterator<@Det Entry<T1, MultiSet<T2>>> iter = map.entrySet().iterator();
        iter.hasNext(); ) {
      Entry<T1, MultiSet<T2>> element = iter.next();
      if (element.getValue().isEmpty()) {
        iter.remove();
      }
    }
  }

  @SuppressWarnings("determinism") // @PolyDet not instantiated correctly in type arguments
  public void removeAllInstances(@Det Set<T2> values) {
    for (@Det MultiSet<T2> multiSet : map.values()) {
      multiSet.removeAllInstances(values);
    }
  }

  public void clear() {
    map.clear();
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }
}
