package randoop.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.checkerframework.checker.determinism.qual.Det;

public class EverythingAllowedPredicate implements ReflectionPredicate {

  @Override
  public boolean test(Class<?> c) {
    return true;
  }

  @Override
  public @Det boolean test(Method m) {
    return true;
  }

  @Override
  public boolean test(Constructor<?> m) {
    return true;
  }

  @Override
  public boolean test(Field f) {
    return true;
  }
}
