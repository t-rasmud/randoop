package randoop.generation;

import org.checkerframework.checker.determinism.qual.Det;
import randoop.operation.TypedOperation;

/**
 * The default implementation of the {@link OperationHistoryLogInterface}. Each method does nothing.
 */
public class DefaultOperationHistoryLogger implements OperationHistoryLogInterface {
  @Override
  public void add(
      @Det DefaultOperationHistoryLogger this,
      @Det TypedOperation operation,
      @Det OperationOutcome outcome) {
    // these methods don't do anything
  }

  @Override
  public void outputTable(@Det DefaultOperationHistoryLogger this) {
    // these methods don't do anything
  }
}
