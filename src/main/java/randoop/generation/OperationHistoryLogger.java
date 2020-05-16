package randoop.generation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.operation.TypedOperation;

// TODO: It's weird to call this a "history log" when it is just a summary, printed at the end of
// execution.
/**
 * The working implementation of a {@link OperationHistoryLogInterface} that will collect and print
 * the operation history log to the {@code PrintWriter} given when constructed.
 */
public class OperationHistoryLogger implements OperationHistoryLogInterface {

  /** The {@code PrintWriter} for outputting the operation history as a table. */
  private final PrintWriter writer;

  /** A sparse representation for the operation-outcome table. */
  private final @PolyDet("upDet") Map<
          @PolyDet TypedOperation, @PolyDet Map<@PolyDet OperationOutcome, @PolyDet Integer>>
      operationMap;

  /**
   * Creates an {@link OperationHistoryLogger} that will write to the given {@code PrintWriter}.
   *
   * @param writer the {@code PrintWriter} for writing the table from the created operation history
   */
  public OperationHistoryLogger(PrintWriter writer) {
    this.writer = writer;
    this.operationMap = new @PolyDet("upDet") HashMap<>();
  }

  @Override
  public void add(
      @Det OperationHistoryLogger this,
      @Det TypedOperation operation,
      @Det OperationOutcome outcome) {
    @Det Map<OperationOutcome, Integer> outcomeMap = operationMap.get(operation);
    @Det int count = 0;
    if (outcomeMap == null) {
      @Det Class<@Det OperationOutcome> tmp = OperationOutcome.class;
      outcomeMap = new EnumMap<OperationOutcome, Integer>(tmp);
    } else {
      Integer countInteger = outcomeMap.get(outcome);
      if (countInteger != null) {
        count = countInteger;
      }
    }
    count += 1;
    outcomeMap.put(outcome, count);
    operationMap.put(operation, outcomeMap);
  }

  @Override
  public void outputTable(@Det OperationHistoryLogger this) {
    writer.format("%nOperation History:%n");
    int maxNameLength = 0;
    for (TypedOperation operation : operationMap.keySet()) {
      @SuppressWarnings("determinism") // process is order insensitive
      @Det int nameLength = operation.getSignatureString().length();
      maxNameLength = Math.max(nameLength, maxNameLength);
    }
    @Det Map<@Det OperationOutcome, @Det String> formatMap = printHeader(maxNameLength);
    @OrderNonDet List<TypedOperation> keys = new ArrayList<>(operationMap.keySet());
    Collections.sort(keys);
    for (TypedOperation key : keys) {
      @SuppressWarnings("determinism") // valid use of Collections.sort
      @Det TypedOperation tmp = key;
      printRow(maxNameLength, formatMap, tmp, operationMap.get(tmp));
    }
    writer.flush();
  }

  /**
   * Writes the header for the operation history table, and constructs a map of format strings for
   * the columns of the table matching the length of the outcome names.
   *
   * @param firstColumnLength the width to use for the first column
   * @return a map from {@link OperationOutcome} value to numeric column format for subsequent rows
   */
  private @Det Map<OperationOutcome, String> printHeader(@Det int firstColumnLength) {
    @Det Class<@Det OperationOutcome> tmp = OperationOutcome.class;
    @Det Map<OperationOutcome, String> formatMap = new EnumMap<OperationOutcome, String>(tmp);
    writer.format("%-" + firstColumnLength + "s", "Operation");
    for (@Det OperationOutcome outcome : OperationOutcome.values()) {
      writer.format("\t%" + outcome.name().length() + "s", outcome);
      formatMap.put(outcome, "\t%" + outcome.name().length() + "d");
    }
    writer.format("%n");
    return formatMap;
  }

  /**
   * Writes a row for a particular operation consisting of the operation signature and the counts of
   * sequences for each outcome.
   *
   * @param firstColumnLength the width to use for the first column
   * @param formatMap the map of format strings for the counts for each outcome
   * @param operation the operation for the row
   * @param countMap the map of counts for the operation and each outcome
   */
  private void printRow(
      int firstColumnLength,
      Map<OperationOutcome, String> formatMap,
      TypedOperation operation,
      Map<OperationOutcome, Integer> countMap) {
    writer.format("%-" + firstColumnLength + "s", operation.getSignatureString());
    for (@Det OperationOutcome outcome : OperationOutcome.values()) {
      Integer count = countMap.get(outcome);
      if (count == null) {
        count = 0;
      }
      writer.format(formatMap.get(outcome), count);
    }
    writer.format("%n");
  }
}
