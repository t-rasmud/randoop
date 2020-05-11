package randoop;

import org.checkerframework.checker.determinism.qual.Det;

/** Means that the statement that this outcome represents was not executed. */
public class NotExecuted extends ExecutionOutcome {

  private static @Det NotExecuted notExecutedSingleton = new NotExecuted();

  private NotExecuted() {
    super(-1);
  }

  public static @Det NotExecuted create() {
    return notExecutedSingleton;
  }

  @Override
  public String toString() {
    return "<not_executed>";
  }

  @Override
  public long getExecutionTime() {
    throw new IllegalStateException("NotExecuted outcome has no execution time.");
  }
}
