package randoop.util;

import org.checkerframework.checker.determinism.qual.Det;

/** Exception for tracking errors in logging that should result in Randoop termination. */
public class RandoopLoggingError extends Error {
  private static final long serialVersionUID = -3641426773814539646L;

  public RandoopLoggingError(@Det String message) {
    super(message);
  }
}
