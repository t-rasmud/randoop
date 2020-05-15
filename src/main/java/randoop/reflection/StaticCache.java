package randoop.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.main.RandoopBug;

/** Stores the mutable state of a class, allowing it to be saved, printed and restored. */
public class StaticCache {

  /** The class for which static state is to be saved. */
  private final Class<?> declaringClass;

  /** The map from static fields to a value. */
  private final Map<@PolyDet Field, @PolyDet Object> valueMap;

  /**
   * Creates the cache object for a particular class. State is not saved until {@link #saveState()}
   * is called.
   *
   * @param declaringClass the class
   */
  public StaticCache(Class<?> declaringClass) {
    this.declaringClass = declaringClass;
    this.valueMap = new @PolyDet LinkedHashMap<>();
  }

  /** Prints the fields and their values to standard output. */
  public void printCache(@Det StaticCache this) {
    for (Map.Entry<Field, Object> entry : valueMap.entrySet()) {
      System.out.println(
          declaringClass.getName() + "." + entry.getKey().getName() + " = " + entry.getValue());
    }
  }

  /** Saves the state for the class in this object. Only saves non-final static fields. */
  public void saveState() {
    for (Field field : declaringClass.getDeclaredFields()) {
      @PolyDet Field tmp = field;
      tmp.setAccessible(true);
      int mods = Modifier.fieldModifiers() & tmp.getModifiers();
      if (Modifier.isStatic(mods) && !Modifier.isFinal(mods)) {
        Object value;
        try {
          value = tmp.get(null);
        } catch (IllegalAccessException e) {
          throw new RandoopBug("unable to save value of field " + tmp.getName());
        }
        valueMap.put(tmp, value);
      }
    }
  }

  /** Restores the saved state of the class in this object to previously saved values. */
  public void restoreState() {
    for (Map.@PolyDet("up") Entry<@PolyDet Field, @PolyDet Object> entry : valueMap.entrySet()) {
      try {
        Map.@PolyDet Entry<@PolyDet Field, @PolyDet Object> tmp = entry;
        tmp.getKey().set(null, entry.getValue());
      } catch (IllegalAccessException e) {
        throw new RandoopBug("unable to save value of field " + entry.getKey());
      }
    }
  }
}
