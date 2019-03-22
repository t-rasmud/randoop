package randoop.generation;

import org.checkerframework.checker.determinism.qual.Det;
import randoop.operation.TypedOperation;

/**
 * Interface for classes that log the usage of operations in the generated sequences. Represents a
 * table of counts indexed by operations and the outcome (as a {@link OperationOutcome}.
 */
public interface OperationHistoryLogInterface {
  /**
   * Increments the count for {@code operation} and {@code outcome}.
   *
   * @param operation the {@link TypedOperation}
   * @param outcome the generation outcome for the operation
   */
  void add(@Det TypedOperation operation, @Det OperationOutcome outcome);

  /**
   * Prints a table showing the counts for each operation-outcome pair in the operation history log.
   */
  void outputTable(@Det OperationHistoryLogInterface this);
}
