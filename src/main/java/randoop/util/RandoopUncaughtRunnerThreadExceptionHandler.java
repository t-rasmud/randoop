package randoop.util;

import java.lang.Thread.UncaughtExceptionHandler;
import org.checkerframework.checker.determinism.qual.Det;

public class RandoopUncaughtRunnerThreadExceptionHandler implements UncaughtExceptionHandler {

  private static @Det RandoopUncaughtRunnerThreadExceptionHandler singleInstance =
      new RandoopUncaughtRunnerThreadExceptionHandler();

  public static @Det UncaughtExceptionHandler getHandler() {
    return singleInstance;
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    // Do nothing.
  }
}
