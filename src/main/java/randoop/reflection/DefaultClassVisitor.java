package randoop.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.checkerframework.checker.determinism.qual.Det;

/** Default implementation of the {@code ClassVisitor} class. All methods do nothing. */
public class DefaultClassVisitor implements ClassVisitor {

  @Override
  public void visit(
      @Det DefaultClassVisitor this, @Det Class<?> c, @Det ReflectionManager reflectionManager) {
    // default is to do nothing
  }

  @Override
  public void visit(@Det DefaultClassVisitor this, @Det Constructor<?> c) {
    // default is to do nothing
  }

  @Override
  public void visit(@Det DefaultClassVisitor this, @Det Method m) {
    // default is to do nothing
  }

  @Override
  public void visit(@Det DefaultClassVisitor this, @Det Field f) {
    // default is to do nothing
  }

  @Override
  public void visit(@Det DefaultClassVisitor this, @Det Enum<?> e) {
    // default is to do nothing
  }

  @Override
  public void visitBefore(@Det DefaultClassVisitor this, @Det Class<?> c) {
    // default is to do nothing
  }

  @Override
  public void visitAfter(Class<?> c) {
    // default is to do nothing
  }

  @Override
  public String toString() {
    return getClass().toString();
  }
}
