package randoop.main;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.generation.AbstractGenerator;
import randoop.reflection.StaticCache;
import randoop.util.ReflectionExecutor;

/** Manages the static state of Randoop classes with Options annotations. */
public class OptionsCache {

  /** The list of caches for classes with Options annotations. */
  private final List<@PolyDet StaticCache> cacheList;

  /** Creates an object for caching the state of command-line arguments. */
  @SuppressWarnings("determinism") // valid rule relaxation: no unintended aliasing, so safe to treat @Det as @PolyDet
  public OptionsCache() {
    cacheList = new @PolyDet ArrayList<>();
    cacheList.add(new StaticCache(GenInputsAbstract.class));
    cacheList.add(new StaticCache(ReflectionExecutor.class));
    cacheList.add(new StaticCache(AbstractGenerator.class));
  }

  /** Prints the saved state of all command-line arguments. */
  public void printState(@Det OptionsCache this) {
    for (@Det StaticCache cache : cacheList) {
      cache.printCache();
    }
  }

  /** Saves the state of all command-line arguments. */
  public void saveState(@Det OptionsCache this) {
    for (@Det StaticCache cache : cacheList) {
      cache.saveState();
    }
  }

  /** Restores the previously saved state of the command-line arguments. */
  public void restoreState(@Det OptionsCache this) {
    for (@Det StaticCache cache : cacheList) {
      cache.restoreState();
    }
  }
}
