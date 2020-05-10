package randoop.main;

import org.checkerframework.checker.determinism.qual.Det;

/** A ClassNameErrorHandler determines the error behavior when a class name error occurs. */
public interface ClassNameErrorHandler {

  /**
   * Performs error handling behavior for bad class name.
   *
   * @param className the name of the class for inclusion in messages
   */
  void handle(@Det String className);

  /**
   * Performs error handling behavior for failure to read class due to exception.
   *
   * @param classname the class name to include in message
   * @param e the exception from loading class
   */
  void handle(@Det String classname, @Det Throwable e);
}
