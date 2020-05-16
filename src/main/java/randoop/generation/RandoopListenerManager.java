package randoop.generation;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.sequence.ExecutableSequence;

@SuppressWarnings("determinism") // @PolyDet("up") same as @PolyDet
public class RandoopListenerManager {

  private List<@PolyDet IEventListener> listeners;

  /** Create a new RandoopListenerManager with no listeners. */
  public RandoopListenerManager() {
    listeners = new @PolyDet ArrayList<>();
  }

  // As of April 2018, this method is never called in Randoop, so the
  // RandoopListenerManager class does nothing in practice.
  public void addListener(IEventListener listener) {
    if (listener == null) {
      throw new IllegalArgumentException("listener is null");
    }
    listeners.add(listener);
  }

  public void generationStepPre() {
    for (IEventListener n : listeners) {
      n.generationStepPre();
    }
  }

  // es can be null.
  public void generationStepPost(ExecutableSequence es) {
    for (IEventListener n : listeners) {
      n.generationStepPost(es);
    }
  }

  public void progressThreadUpdateNotify() {
    for (IEventListener n : listeners) {
      n.progressThreadUpdate();
    }
  }

  public boolean shouldStopGeneration() {
    for (IEventListener n : listeners) {
      if (n.shouldStopGeneration()) {
        return true;
      }
    }
    return false;
  }

  public void explorationStart() {
    for (IEventListener n : listeners) {
      n.explorationStart();
    }
  }

  public void explorationEnd() {
    for (IEventListener n : listeners) {
      n.explorationEnd();
    }
  }
}
