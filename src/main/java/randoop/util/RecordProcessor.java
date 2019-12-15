package randoop.util;

import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** Processes a single record given by RecordListReader. */
public interface RecordProcessor {

  /**
   * Parse the given lines that comprise a record.
   *
   * @param record the lines of a record
   */
  void processRecord(List<@PolyDet String> record);
}
