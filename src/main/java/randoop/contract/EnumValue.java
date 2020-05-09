package randoop.contract;

import java.util.Collections;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import randoop.types.Type;
import randoop.types.TypeTuple;

/**
 * A check for a particular value of an {@code Enum}. To be used in regression tests when {@code
 * Enum} values are created.
 */
public final class EnumValue extends ObjectContract {

  public final Enum<?> value;
  private final Type type;

  public EnumValue(Enum<?> value) {
    this.value = value;
    this.type = Type.forClass(value.getDeclaringClass());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof EnumValue)) {
      return false;
    }
    @SuppressWarnings("determinism:invariant.cast.unsafe")
    EnumValue enumValue = (EnumValue) obj;
    return value.equals(enumValue.value);
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "randoop.contract.EnumValue = " + value.name();
  }

  @Override
  public int getArity() {
    return 1;
  }

  @Override
  @SuppressWarnings("determinism:override.return.invalid")
  public TypeTuple getInputTypes() {
    return new TypeTuple(Collections.singletonList(type));
  }

  @Override
  public boolean evaluate(Object... objects) throws Throwable {
    assert objects.length == 1;
    return value.equals(objects[0]);
  }

  @Override
  public String toCommentString() {
    return null;
  }

  public String getValueName() {
    return type.getName() + "." + value.name();
  }

  @Override
  public String toCodeString() {
    String valueName = getValueName();
    StringBuilder b = new StringBuilder();
    b.append("org.junit.Assert.assertTrue(");
    b.append("\"'\" + " + "x0" + " + \"' != '\" + ").append(valueName).append(" + \"'\", ");
    b.append("x0");
    b.append(".equals(");
    b.append(valueName);
    b.append(")");
    b.append(");");
    return b.toString();
  }

  @Override
  public String get_observer_str() {
    return "EnumValue";
  }
}
