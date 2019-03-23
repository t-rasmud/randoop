package randoop.main;

import org.checkerframework.checker.determinism.qual.Det;

/** Exception for classname errors. */
public class RandoopClassNameError extends Error {
  private static final long serialVersionUID = -3625971508842588810L;

  public String className;

  public RandoopClassNameError(@Det String className, String message) {
    super(message);
    this.className = className;
  }

  public RandoopClassNameError(@Det String className, String message, Throwable cause) {
    super(message, cause);
    this.className = className;
  }
}
