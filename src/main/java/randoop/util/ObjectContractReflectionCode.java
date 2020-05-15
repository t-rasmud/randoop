package randoop.util;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.contract.ObjectContract;

public final class ObjectContractReflectionCode extends ReflectionCode {

  final ObjectContract c;
  final @PolyDet Object[] objs;

  public ObjectContractReflectionCode(
      final @PolyDet ObjectContract c, final @PolyDet Object @PolyDet ... objs) {
    this.c = c;
    this.objs = objs;
  }

  @Override
  protected void runReflectionCodeRaw() {
    try {
      @PolyDet Object tmp = c.evaluate(objs);
      retval = tmp;
    } catch (Throwable e) {
      exceptionThrown = e;
    }
  }

  @Override
  @SuppressWarnings(
      "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
  // @PolyDet
  public String toString() {
    return "Check of ObjectContract " + c + " args: " + Arrays.toString(objs) + status();
  }
}
