package randoop.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import java.util.List;
import java.util.StringJoiner;
import org.checkerframework.checker.formatter.qual.FormatMethod;
import org.checkerframework.dataflow.qual.SideEffectFree;
import org.plumelib.util.UtilPlume;
import randoop.main.GenInputsAbstract;
import randoop.main.RandoopBug;

/** Static methods that log to GenInputsAbstract.log, if that is non-null. */
public final class Log {

  private Log() {
    throw new IllegalStateException("no instance");
  }

  public static boolean isLoggingOn() {
    return GenInputsAbstract.log != null;
  }

  /**
   * Log using {@code String.format} to GenInputsAbstract.log, if that is non-null.
   *
   * @param fmt the format string
   * @param args arguments to the format string
   */
  @FormatMethod
  @RequiresDetToString
  @SuppressWarnings("determinism") // https://github.com/typetools/checker-framework/issues/3277
  public static void logPrintf(@Det String fmt, @Det Object @Det ... args) {
    if (!isLoggingOn()) {
      return;
    }

    String msg;
    try {
      String tmp = String.format(fmt, args);
      msg = tmp;
    } catch (Throwable t) {
      logPrintf("A user-defined toString() method failed.%n");
      Class<?>[] argTypes = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) {
        argTypes[i] = args[i].getClass();
      }
      logPrintf("  fmt = %s%n", fmt);
      logPrintf("  arg types = %s%n", Arrays.toString(argTypes));
      logStackTrace(t);
      return;
    }

    try {
      GenInputsAbstract.log.write(msg);
      GenInputsAbstract.log.flush();
    } catch (IOException e) {
      throw new RandoopBug("Exception while writing to log", e);
    }
  }

  /**
   * Log to GenInputsAbstract.log, if that is non-null.
   *
   * @param t the Throwable whose stack trace to log
   */
  public static void logStackTrace(Throwable t) {
    if (!isLoggingOn()) {
      return;
    }

    try {
      // Gross, GenInputsAbstract.log should be a writer instead of a FileWriter
      PrintWriter pw = new PrintWriter(GenInputsAbstract.log);
      t.printStackTrace(pw);
      pw.flush();
      GenInputsAbstract.log.flush();
    } catch (IOException e) {
      throw new RandoopBug("Exception while writing to log", e);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  /// Debugging toString
  ///

  /// TODO: Move these methods to UtilPlume.  (Synchronize the two versions before deleting this
  // one.  Also write tests, probably.)

  /**
   * Gives a string representation of the value and its class. Intended for debugging.
   *
   * @param v a value; may be null
   * @return the value's toString and its class
   */
  @SideEffectFree
  public static String toStringAndClass(Object v) {
    return toStringAndClass(v, false);
  }

  /**
   * Gives a string representation of the value and its class. Intended for debugging.
   *
   * @param v a value; may be null
   * @param shallow if true, do not show elements of arrays and lists
   * @return the value's toString and its class
   */
  @SideEffectFree
  private static String toStringAndClass(Object v, boolean shallow) {
    if (v == null) {
      return "null";
    } else if (v.getClass() == Object.class) {
      return "a value of class " + v.getClass();
    } else if (v.getClass().isArray()) {
      return arrayToStringAndClass(v);
    } else if (v instanceof List) {
      return listToStringAndClass((List<? extends @PolyDet Object>) v, shallow);
    } else {
      try {
        @SuppressWarnings("determinism") // underlying value toString is deterministic: v is a value, which implies primitive type or String
        @Det String formatted = UtilPlume.escapeJava(v.toString());
        return String.format("%s [%s]", formatted, v.getClass()); } catch (Exception e) {
        return String.format("exception_when_calling_toString [%s]", v.getClass());
      }
    }
  }

  /**
   * Gives a string representation of the value and its class. Intended for debugging.
   *
   * @param lst a value; may be null
   * @param shallow if false, show the value and class of list elements; if true, do not recurse
   *     into elements of arrays and lists;
   * @return the value's toString and its class
   */
  @SideEffectFree
  @SuppressWarnings("determinism") // underlying value toString is deterministic: lst contains values, which have @Det toString methods
  public static String listToStringAndClass(List<? extends @PolyDet Object> lst, boolean shallow) {
    if (lst == null) {
      return "null";
    } else {
      return listToString(lst, false) + " [" + lst.getClass() + "]";
    }
  }

  /**
   * For use by toStringAndClass. Calls toStringAndClass on each element, but does not add the class
   * of the list itself.
   *
   * @param lst the list to print
   * @param shallow if true, just use {@code toString} on the whole list
   * @return a string representation of each element and its class
   */
  @SideEffectFree
  public static @NonDet String listToString(List<? extends @PolyDet Object> lst, boolean shallow) {
    if (lst == null) {
      return "null";
    } else if (shallow) {
      return lst.toString();
    }
    StringJoiner sj = new StringJoiner(", ", "[", "]");
    for (Object o : lst) {
      sj.add(toStringAndClass(o, true));
    }
    return sj.toString();
  }

  /**
   * Returns a string representation of the contents of the specified array. The argument must be an
   * array or null. This just dispatches one of the 9 overloaded versions of {@code
   * java.util.Arrays.toString()}.
   *
   * @param a an array
   * @return a string representation of the array
   * @throws IllegalArgumentException if a is not an array
   */
  @SuppressWarnings("all:purity") // defensive coding: throw exception when argument is invalid
  @SideEffectFree
  public static String arrayToStringAndClass(Object a) {

    if (a == null) {
      return "null";
    }
    String theClass = " [" + a.getClass() + "]";

    if (a instanceof boolean[]) {
      @PolyDet String tmp = Arrays.toString((boolean[]) a) + theClass;
      return tmp;
    } else if (a instanceof byte[]) {
      @PolyDet String tmp = Arrays.toString((byte[]) a) + theClass;
      return tmp;
    } else if (a instanceof char[]) {
      @PolyDet String tmp = Arrays.toString((char[]) a) + theClass;
      return tmp;
    } else if (a instanceof double[]) {
      @PolyDet String tmp = Arrays.toString((double[]) a) + theClass;
      return tmp;
    } else if (a instanceof float[]) {
      @PolyDet String tmp = Arrays.toString((float[]) a) + theClass;
      return tmp;
    } else if (a instanceof int[]) {
      @PolyDet String tmp = Arrays.toString((int[]) a) + theClass;
      return tmp;
    } else if (a instanceof long[]) {
      @PolyDet String tmp = Arrays.toString((long[]) a) + theClass;
      return tmp;
    } else if (a instanceof short[]) {
      @PolyDet String tmp = Arrays.toString((short[]) a) + theClass;
      return tmp;
    }

    if (a instanceof Object[]) {
      try {
        @SuppressWarnings("determinism") // underlying value toString is deterministic: the array will containd values, which have @Det toString
        @PolyDet String tmp = listToString(Arrays.asList((Object[]) a), false) + theClass;
        return tmp;
      } catch (Exception e) {
        return String.format("exception_when_printing_array" + theClass);
      }
    }

    throw new IllegalArgumentException(
        "Argument is not an array; its class is " + a.getClass().getName());
  }
}
