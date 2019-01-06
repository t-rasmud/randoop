package randoop.util;

import org.checkerframework.checker.determinism.qual.Det;

/** Indicates that a test execution timed out. */
public final class TimeoutExceededException extends RuntimeException {

  private static final long serialVersionUID = 7932531804127083492L;

  public TimeoutExceededException() {}

  public TimeoutExceededException(@Det String string) {
    super(string);
  }
}
