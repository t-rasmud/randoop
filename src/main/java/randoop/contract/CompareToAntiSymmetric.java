package randoop.contract;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.Globals;
import randoop.types.JavaTypes;
import randoop.types.TypeTuple;

/**
 * The contract: Checks that an object is antisymmetric over compareTo.
 *
 * <pre>Math.signum(x0.compareTo(x1)) == -Math.signum(x1.compareTo(x0))</pre>
 */
public class CompareToAntiSymmetric extends ObjectContract {
  private static final @Det CompareToAntiSymmetric instance = new CompareToAntiSymmetric();

  private CompareToAntiSymmetric() {}

  public static @Det CompareToAntiSymmetric getInstance() {
    return instance;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public boolean evaluate(Object... objects) {
    Object o1 = objects[0];
    Object o2 = objects[1];

    // If o1 and o2 are comparable objects, check that their signs are opposite of one another
    if (o1 instanceof Comparable && o2 instanceof Comparable) {
      Comparable compObj1 = (Comparable) o1;
      Comparable compObj2 = (Comparable) o2;

      @SuppressWarnings(
          "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
                         // @PolyDet
      @PolyDet boolean tmp =
          Math.signum(compObj1.compareTo(compObj2)) == -Math.signum(compObj2.compareTo(compObj1));
      return tmp;
    }
    // If the compare to operation can't be done, the statement is trivially true
    return true;
  }

  @Override
  public int getArity() {
    return 2;
  }

  /** The arguments to which this contract can be applied. */
  static TypeTuple inputTypes =
      new TypeTuple(Arrays.asList(JavaTypes.COMPARABLE_TYPE, JavaTypes.COMPARABLE_TYPE));

  @Override
  public @Det TypeTuple getInputTypes() {
    return inputTypes;
  }

  @Override
  public String toCommentString(@Det CompareToAntiSymmetric this) {
    return "compareTo-anti-symmetric on x0 and x1";
  }

  @Override
  public String get_observer_str() {
    return "CompareToAntiSymmetric";
  }

  @Override
  public String toCodeString(@Det CompareToAntiSymmetric this) {
    StringBuilder b = new StringBuilder();
    b.append(Globals.lineSep);
    b.append("// Checks the contract: ");
    b.append(" ").append(toCommentString()).append(Globals.lineSep);
    b.append("org.junit.Assert.assertTrue(");
    b.append("\"Contract failed: ").append(toCommentString()).append("\", ");
    b.append("Math.signum(x0.compareTo(x1)) == -Math.signum(x1.compareTo(x0))");
    b.append(");");
    return b.toString();
  }
}
