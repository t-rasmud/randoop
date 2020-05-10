package randoop.generation;

import java.util.LinkedHashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class EqualsMethodMatcher implements StateMatcher {

  private final Set<@PolyDet Object> cache = new @PolyDet LinkedHashSet<>();

  @Override
  public boolean add(Object object) {
    try {
      return this.cache.add(object);
    } catch (Throwable e) {
      // This could happen, because we're actually running code under test.
      return false;
    }
  }

  @Override
  public int size() {
    return this.cache.size();
  }
}
