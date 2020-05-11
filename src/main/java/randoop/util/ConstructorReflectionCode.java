package randoop.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** Wraps a constructor together with its arguments, ready for execution. Can be run only once. */
public final class ConstructorReflectionCode extends ReflectionCode {

  /** The constructor to be called. */
  private final @PolyDet Constructor<?> constructor;
  /**
   * The arguments that the constructor is applied to. If an inner class constructor has a receiver,
   * it is the first element of this array.
   */
  private final Object @PolyDet [] inputs;

  /**
   * Create a new ConstructorReflectionCode to represent a constructor invocation.
   *
   * @param constructor the constructor to be called
   * @param inputs the arguments that the constructor is applied to. If an inner class constructor
   *     has a receiver, it is the first element of this array.
   */
  @SuppressWarnings("deprecation") // AccessibleObject.isAccessible() has no replacement in Java 8.
  public ConstructorReflectionCode(Constructor<?> constructor, Object[] inputs) {
    if (constructor == null) {
      throw new IllegalArgumentException("constructor is null");
    }
    if (inputs == null) {
      throw new IllegalArgumentException("inputs is null");
    }
    this.constructor = constructor;
    this.inputs = inputs;

    if (!this.constructor.isAccessible()) {
      this.constructor.setAccessible(true);
      Log.logPrintf("not accessible: %s%n", this.constructor);
      // TODO something is bizarre - it seems that a public method can be
      // not-accessible sometimes. RatNum(int,int)
      // TODO you cannot just throw the exception below - because no sequences
      // will be created in the randoop.experiments.
      // throw new IllegalStateException("Not accessible: " + this.constructor);
    }
  }

  @SuppressWarnings("Finally")
  @Override
  public void runReflectionCodeRaw() {
    try {
      @SuppressWarnings("determinism") // this is code randoop is run on
      @PolyDet Object tmp = this.constructor.newInstance(this.inputs);
      this.retval = tmp;
    } catch (InvocationTargetException e) {
      // The underlying constructor threw an exception
      this.exceptionThrown = e.getCause();
    } catch (Throwable e) {
      // Any other exception indicates Randoop should not have called the constructor
      throw new ReflectionCodeException(e);
    }
  }

  @Override
  @SuppressWarnings(
      "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
                     // @PolyDet
  public String toString() {
    return "Call to " + constructor + ", args: " + Arrays.toString(inputs) + status();
  }
}
