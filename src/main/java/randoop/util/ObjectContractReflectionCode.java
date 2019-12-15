package randoop.util;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.contract.ObjectContract;

public final class ObjectContractReflectionCode extends ReflectionCode {

  final ObjectContract c;
  final Object[] objs;

  public ObjectContractReflectionCode(
      final @PolyDet ObjectContract c, final @PolyDet Object @PolyDet ... objs) {
    this.c = c;
    this.objs = objs;
  }

  @Override
  protected void runReflectionCodeRaw() {
    try {
      retval = c.evaluate(objs);
    } catch (Throwable e) {
      exceptionThrown = e;
    }
  }

  @Override
  public String toString() {
    return "Check of ObjectContract " + c + " args: " + Arrays.toString(objs) + status();
  }
}
