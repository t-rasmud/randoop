package randoop.contract;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.Globals;
import randoop.types.JavaTypes;
import randoop.types.TypeTuple;

/** The contract: {@code o1.equals(o2) ==> o2.equals(o1)}. */
public final class EqualsSymmetric extends ObjectContract {
  private static final @Det EqualsSymmetric instance = new EqualsSymmetric();

  private EqualsSymmetric() {}

  public static @Det EqualsSymmetric getInstance() {
    return instance;
  }

  @Override
  public boolean evaluate(Object... objects) {

    Object o1 = objects[0];
    Object o2 = objects[1];

    @SuppressWarnings(
        "determinism") // varargs can't be @OrderNonDet so @PolyDet("up") same as @PolyDet
    @PolyDet boolean tmp = !o1.equals(o2) || o2.equals(o1);
    return tmp;
  }

  @Override
  public int getArity() {
    return 2;
  }

  /** The arguments to which this contract can be applied. */
  static TypeTuple inputTypes =
      new TypeTuple(Arrays.asList(JavaTypes.OBJECT_TYPE, JavaTypes.OBJECT_TYPE));

  @Override
  public @Det TypeTuple getInputTypes() {
    return inputTypes;
  }

  @Override
  public String toCommentString(@Det EqualsSymmetric this) {
    return "equals-symmetric on x0 and x1.";
  }

  @Override
  public String get_observer_str() {
    return "equals-symmetric";
  }

  @Override
  public String toCodeString(@Det EqualsSymmetric this) {
    StringBuilder b = new StringBuilder();
    b.append(Globals.lineSep);
    b.append("// This assertion (symmetry of equals) fails ");
    b.append(Globals.lineSep);
    b.append("org.junit.Assert.assertTrue(");
    b.append("\"Contract failed: " + toCommentString() + "\", ");
    b.append("x0.equals(x1) == x1.equals(x0)");
    b.append(");");
    return b.toString();
  }
}
