package randoop.types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.plumelib.util.UtilPlume;

/**
 * Represents a parameterized type. A <i>parameterized type</i> is a type {@code C<T1,...,Tk>} where
 * {@code C<F1,...,Fk>} is a generic class instantiated by a substitution {@code [Fi:=Ti]}, and
 * {@code Ti} is a subtype of the upper bound {@code Bi} of the type parameter {@code Fi}.
 *
 * @see GenericClassType
 * @see InstantiatedType
 */
public abstract class ParameterizedType extends ClassOrInterfaceType {

  /** A cache of all ParameterizedTypes that have been created. */
  private static final @OrderNonDet Map<@Det Class<?>, @Det GenericClassType> cache =
      new HashMap<>();

  /**
   * Creates a {@link GenericClassType} for the given reflective {@link Class} object.
   *
   * @param typeClass the class type
   * @return a generic class type for the given type
   */
  public static @Det GenericClassType forClass(@Det Class<?> typeClass) {
    if (typeClass.getTypeParameters().length == 0) {
      throw new IllegalArgumentException(
          "class must be a generic type, have " + typeClass.getName());
    }
    @Det GenericClassType cached = cache.get(typeClass);
    if (cached == null) {
      cached = new GenericClassType(typeClass);
      // @SuppressWarnings("determinism") // second argument expects @OrderNonDet becaus of Map
      // annotation
      @Det GenericClassType tmp = cache.put(typeClass, cached);
    }
    return cached;
  }

  /**
   * Performs the conversion of {@code java.lang.reflect.ParameterizedType} to a {@code
   * ParameterizedType} .
   *
   * @param type the reflective type object
   * @return an object of type {@code ParameterizedType}
   */
  public static @Det ParameterizedType forType(java.lang.reflect.@Det Type type) {
    if (!(type instanceof java.lang.reflect.ParameterizedType)) {
      throw new IllegalArgumentException("type must be java.lang.reflect.ParameterizedType");
    }

    java.lang.reflect.ParameterizedType t = (java.lang.reflect.ParameterizedType) type;
    Type rawType = t.getRawType();
    assert (rawType instanceof Class<?>) : "rawtype not an instance of Class<?> type ";

    // Categorize the type arguments as either a type variable or other kind of argument
    List<TypeArgument> typeArguments = new ArrayList<>();
    for (@Det Type argType : t.getActualTypeArguments()) {
      @Det TypeArgument argument = TypeArgument.forType(argType);
      typeArguments.add(argument);
    }

    // When building parameterized type, first create generic class from the
    // rawtype, and then instantiate with the arguments collected from the
    // java.lang.reflect.ParameterizedType interface.
    @Det GenericClassType genericClass = ParameterizedType.forClass((Class<?>) rawType);
    return new InstantiatedType(genericClass, typeArguments);
  }

  @Override
  public String toString() {
    return this.getName();
  }

  @Override
  public abstract @Det ParameterizedType substitute(
      @Det ParameterizedType this, @Det Substitution substitution);

  /**
   * Returns the {@link GenericClassType} for this parameterized type.
   *
   * @return the generic class type for this type
   */
  public abstract GenericClassType getGenericClassType();

  /**
   * {@inheritDoc}
   *
   * <p>Returns the fully-qualified name of this type with fully-qualified type arguments. E.g.,
   * {@code java.lang.List<java.lang.String>}
   */
  @Override
  public String getName() {
    @PolyDet String tmp = super.getName() + "<" + UtilPlume.join(this.getTypeArguments(), ",") + ">";
    return tmp;
  }

  @Override
  public String getUnqualifiedName() {
    @PolyDet String tmp = this.getSimpleName() + "<" + UtilPlume.join(this.getTypeArguments(), ",") + ">";
    return tmp;
  }
}
