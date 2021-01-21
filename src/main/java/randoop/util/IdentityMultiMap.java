package randoop.util;

import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

/** A multi-map using key identity rather than equality. */
@HasQualifierParameter(NonDet.class)
@SuppressWarnings("determinism") // valid rule relaxation: retreiving a @PolyDet Set value from a @PolyDet("upDet") map gives a @PolyDet("upDet") set, but should be @PolyDet.
public class IdentityMultiMap<K extends @PolyDet Object, V extends @PolyDet Object> {

  /** the underlying map */
  private @PolyDet("upDet") IdentityHashMap<K, @PolyDet Set<V>> map;

  /** Creates an empty multi-map. */
  public IdentityMultiMap() {
    map = new @PolyDet("upDet") IdentityHashMap<>();
  }

  /**
   * Adds a key-value pair to the multimap.
   *
   * @param key the key
   * @param value the value
   */
  public void put(K key, V value) {
    @PolyDet Set<V> set = map.get(key);
    if (set == null) {
      set = new @PolyDet LinkedHashSet<>();
      map.put(key, set);
    }
    set.add(value);
  }

  /**
   * Returns the set of values that correspond to the given key in the map.
   *
   * @param key the key value
   * @return the set of values that correspond to the key, null if none
   */
  public Set<V> get(K key) {
    return map.get(key);
  }
}
