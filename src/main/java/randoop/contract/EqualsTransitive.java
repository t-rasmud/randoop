package randoop.contract;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import randoop.Globals;
import randoop.types.JavaTypes;
import randoop.types.TypeTuple;

/**
 * The contract: Checks the transitivity of equals for an object {@code (x0.equals(x1) &&
 * x1.equals(x2)) ==> x0.equals(x2)}.
 */
public class EqualsTransitive extends ObjectContract {
  private static final @Det EqualsTransitive instance = new EqualsTransitive();

  private EqualsTransitive() {}

  public static @Det EqualsTransitive getInstance() {
    return instance;
  }

  @Override
  @SuppressWarnings(
      "determinism") // varargs can't be @OrderNonDet so @PolyDet("up") same as @PolyDet
  public boolean evaluate(Object... objects) {
    Object o1 = objects[0];
    Object o2 = objects[1];
    Object o3 = objects[2];

    return !(o1.equals(o2) && o2.equals(o3)) || o1.equals(o3);
  }

  @Override
  public int getArity() {
    return 3;
  }

  /** The arguments to which this contract can be applied. */
  static TypeTuple inputTypes =
      new TypeTuple(
          Arrays.asList(JavaTypes.OBJECT_TYPE, JavaTypes.OBJECT_TYPE, JavaTypes.OBJECT_TYPE));

  @Override
  public @Det TypeTuple getInputTypes() {
    return inputTypes;
  }

  @Override
  public String toCommentString(@Det EqualsTransitive this) {
    return "equals-transitive on x0, x1, and x2.";
  }

  @Override
  public String get_observer_str() {
    return "equals-transitive";
  }

  @Override
  public String toCodeString(@Det EqualsTransitive this) {
    StringBuilder b = new StringBuilder();
    b.append(Globals.lineSep);
    b.append("// This assertion (transitivity of equals) fails ");
    b.append(Globals.lineSep);
    b.append("org.junit.Assert.assertTrue(");
    b.append("\"Contract failed: " + toCommentString() + "\", ");
    b.append("!(x0.equals(x1) && x1.equals(x2)) || x0.equals(x2)");
    b.append(");");
    return b.toString();
  }
}
