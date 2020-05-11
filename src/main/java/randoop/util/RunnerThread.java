package randoop.util;

import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.NoQualifierParameter;

public class RunnerThread extends Thread {

  // Fields assigned when calling setup(..)
  private @PolyDet ReflectionCode code;

  // Fields assigned when calling run()
  @PolyDet boolean runFinished;

  // The state of the thread.
  private @PolyDet NextCallMustBe state;

  @NoQualifierParameter(NonDet.class)
  private enum NextCallMustBe {
    SETUP,
    RUN
  }

  /**
   * Create a new runner thread.
   *
   * @param threadGroup the group for this thread
   */
  RunnerThread(@PolyDet ThreadGroup threadGroup) {
    super(threadGroup, "randoop.util.RunnerThread");
    this.code = null;
    this.runFinished = false;
    this.state = NextCallMustBe.SETUP;
    this.setUncaughtExceptionHandler(RandoopUncaughtRunnerThreadExceptionHandler.getHandler());
  }

  public void setup(ReflectionCode code) {
    if (state != NextCallMustBe.SETUP) throw new IllegalStateException();
    if (code == null) throw new IllegalArgumentException("code cannot be null.");
    this.code = code;
    this.state = NextCallMustBe.RUN;
  }

  @Override
  public final void run(@Det RunnerThread this) {
    if (state != NextCallMustBe.RUN) throw new IllegalStateException();
    runFinished = false;
    executeReflectionCode();
    runFinished = true;
    this.state = NextCallMustBe.SETUP;
  }

  private void executeReflectionCode(@Det RunnerThread this)
      throws ReflectionCode.ReflectionCodeException {
    code.runReflectionCode();
  }

  /**
   * Return the ReflectionCode that is being, or was, run.
   *
   * @return the ReflectionCode that is being, or was, run
   */
  public ReflectionCode getCode() {
    return code;
  }
}
