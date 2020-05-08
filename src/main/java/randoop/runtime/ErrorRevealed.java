package randoop.runtime;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class ErrorRevealed implements IMessage {

  public final String testCode;
  public final String description;
  public final Path junitFile;

  // Unmodifiable collection.
  public final List<@PolyDet String> failingClassNames;

  public ErrorRevealed(
      String testCode, String description, @PolyDet List<@PolyDet String> failingClassNames, Path junitFile) {
    this.testCode = testCode;
    this.description = description;
    @PolyDet List<@PolyDet String> tmp = new @PolyDet ArrayList<>(failingClassNames);
    this.failingClassNames = Collections.unmodifiableList(tmp);
    this.junitFile = junitFile;
  }

  private static final long serialVersionUID = -9131735651851725022L;

  @Override
  public String toString() {
    return description;
  }

  public List<@PolyDet String> getFailingClassNames() {
    return failingClassNames;
  }
}
