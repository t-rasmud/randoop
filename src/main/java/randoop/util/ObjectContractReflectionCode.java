package randoop.util;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import randoop.contract.ObjectContract;

public final class ObjectContractReflectionCode extends ReflectionCode {

  final ObjectContract c;
  final Object[] objs;

  public ObjectContractReflectionCode(final @Det ObjectContract c, final @Det Object... objs) {
    this.c = c;
    this.objs = objs;
  }

  @Override
  protected void runReflectionCodeRaw(@Det ObjectContractReflectionCode this) {
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
