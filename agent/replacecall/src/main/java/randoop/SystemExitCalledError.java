package randoop;

import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

/**
 * Exception to be thrown by default replacement for {@code System.exit()}.
 *
 * @see randoop.mock.java.lang.System
 */
@HasQualifierParameter(NonDet.class)
public class SystemExitCalledError extends Error {

  /** status value for System.exit() call */
  private final @PolyDet int status;

  public SystemExitCalledError(int status) {
    super(String.format("Call to System exit(%d) detected; terminating execution", status));
    this.status = status;
    // If there are any java.awt windows active java runtime will deadlock.
    for (java.awt.Window w : java.awt.Window.getWindows()) {
      w.dispose();
    }
  }
}
