package randoop.util;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import randoop.contract.ObjectContract;

public final class ObjectContractReflectionCode extends ReflectionCode {

  final ObjectContract c;
  final Object[] objs;

  public ObjectContractReflectionCode(final @Det ObjectContract c, final @Det Object... objs) {
    this.c = c;
    @SuppressWarnings("determinism") // variable argument list can't be declared @Det, but its order
    // is decided at compile time so it's @Det anyway.
    @Det
    Object @Det [] tmp = objs;
    this.objs = tmp;
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
    @SuppressWarnings("determinism") // For some reason, calling status() is @NonDet no matter what.
    @Det
    String status = status();
    return "Check of ObjectContract " + c + " args: " + Arrays.toString(objs) + status;
  }
}
