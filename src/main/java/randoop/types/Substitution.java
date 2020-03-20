package randoop.types;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

/**
 * A substitution maps type parameters/variables (including wildcards) to concrete types. It
 * represents the instantiation of a generic class to a parameterized type.
 */
public class Substitution {

  /** The map from type variables to concrete types. */
  private Map<@PolyDet TypeVariable, @PolyDet ReferenceType> map;

  /**
   * Map on reflection types - used for testing bounds. Its keys are a subset of the keys of {@link
   * #map}: those that are type parameters as opposed to other type variables such as wildcards.
   */
  private Map<java.lang.reflect.@PolyDet Type, @PolyDet ReferenceType> rawMap;

  /** Create an empty substitution. */
  public Substitution() {
    map = new @PolyDet LinkedHashMap<>();
    rawMap = new @PolyDet LinkedHashMap<>();
  }

  /**
   * Make a copy of the given substitution.
   *
   * @param substitution the substitution to copy
   */
  public Substitution(@PolyDet Substitution substitution) {
    map = new @PolyDet LinkedHashMap<>(substitution.map);
    rawMap = new @PolyDet LinkedHashMap<>(substitution.rawMap);
  }

  /**
   * Create a substitution that maps the given type parameter to the given type argument.
   *
   * @param parameter the type parameter
   * @param argument the type argument
   */
  public Substitution(@PolyDet TypeVariable parameter, @PolyDet ReferenceType argument) {
    this();
    put(parameter, argument);
  }

  /**
   * Create a substitution from the type parameters to the corresponding type arguments.
   *
   * @param parameters the type parameters
   * @param arguments the type arguments
   */
  public Substitution(
      @PolyDet List<@PolyDet TypeVariable> parameters,
      @PolyDet ReferenceType @PolyDet ... arguments) {
    this();
    assert parameters.size() == arguments.length
        : "parameters=" + parameters + "  arguments=" + Arrays.toString(arguments);
    for (int i = 0; i < parameters.size(); i++) {
      @SuppressWarnings("determinism") // iterating over @PolyDet collection to create another
      @PolyDet TypeVariable tmp1 = parameters.get(i);
      @SuppressWarnings("determinism") // iterating over @PolyDet collection to create another
      @PolyDet ReferenceType tmp2 = arguments[i];
      put(tmp1, tmp2);
    }
  }

  /**
   * Create a substitution from the type parameters to the corresponding type arguments.
   *
   * @param parameters the type parameters
   * @param arguments the type arguments
   */
  public Substitution(
      @PolyDet List<@PolyDet TypeVariable> parameters,
      @PolyDet List<@PolyDet ReferenceType> arguments) {
    this();
    assert parameters.size() == arguments.size();
    for (int i = 0; i < parameters.size(); i++) {
      @SuppressWarnings("determinism") // iterating over @PolyDet collection to create another
      @PolyDet TypeVariable tmp1 = parameters.get(i);
      @SuppressWarnings("determinism") // iterating over @PolyDet collection to create another
      @PolyDet ReferenceType tmp2 = arguments.get(i);
      put(tmp1, tmp2);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @return true if the substitution maps are identical and false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof Substitution)) {
      return false;
    }
    @SuppressWarnings("determinism") // casting here doesn't change the determinism type
    Substitution s = (Substitution) obj;
    @SuppressWarnings(
        "determinism") // method parameters can't be @OrderNonDet so @PolyDet("up") is the same as
                       // @PolyDet
    @PolyDet boolean tmp = map.equals(s.map);
    return tmp;
  }

  @Override
  public @NonDet int hashCode() {
    return Objects.hash(map);
  }

  @Override
  public @NonDet String toString() {
    return map.toString();
  }

  /**
   * Two substitutions are consistent if their type variables are disjoint or, if they both map the
   * same type variable, they map it to the same type. This is the test for whether this
   * substitution can be extended by the other substitution using {@link #extend(Substitution)}.
   *
   * @param substitution the other substitution to check for consistency with this substitution
   * @return true if the the substitutions are consistent, false otherwise
   */
  public boolean isConsistentWith(Substitution substitution) {
    for (
    @PolyDet Entry<@PolyDet TypeVariable, @PolyDet ReferenceType> entry : substitution.map.entrySet()) {
      if (this.map.containsKey(entry.getKey())
          && !this.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }
    for (
    @PolyDet Entry<java.lang.reflect.@PolyDet Type, @PolyDet ReferenceType> entry :
        substitution.rawMap.entrySet()) {
      if (this.rawMap.containsKey(entry.getKey())
          && !this.get(entry.getKey()).equals(entry.getValue())) {
        return false;
      }
    }
    return true;
  }

  /** Throws an exception if its arguments are different non-null values. */
  private static BiFunction<ReferenceType, ReferenceType, ReferenceType> requireSameEntry =
      (v1, v2) -> {
        if (v1 == null) return v2;
        if (v2 == null) return v1;
        if (v1.equals(v2)) return v1;
        throw new IllegalArgumentException(
            String.format("Substitutions map a key to distinct types %s and %s", v1, v2));
      };

  /**
   * Creates a new substitution that contains the mappings of this substitution, extended by the
   * given mappings. If this and the additional mappings contain the same type variable, both must
   * map it to the same type.
   *
   * @param parameters the type parameters
   * @param arguments the type arguments
   * @return a new substitution that is this substitution extended by the given mappings
   */
  public Substitution extend(
      @PolyDet List<@PolyDet TypeVariable> parameters,
      @PolyDet List<@PolyDet ReferenceType> arguments) {
    return extend(new Substitution(parameters, arguments));
  }

  /**
   * Creates a new substitution that contains the entries of two substitutions. If both
   * substitutions contain the same type variable, they must map to the same type.
   *
   * @param other the substitution to add to this substitution
   * @return a new substitution that is this substitution extended by the given substitution
   */
  @SuppressWarnings(
      "determinism") // @PolyDet("use") same as @PolyDet so for each loop assignment compatible
  public Substitution extend(@PolyDet("use") Substitution other) {
    Substitution result = new Substitution(this);
    for (
    @PolyDet("down") Entry<@PolyDet TypeVariable, @PolyDet ReferenceType> entry : other.map.entrySet()) {
      @SuppressWarnings(
          "determinism") // The fact the function requiredEntry is @Det is clearly not an issue
      ReferenceType ignore = result.map.merge(entry.getKey(), entry.getValue(), requireSameEntry);
    }
    for (
    @PolyDet("down") Entry<java.lang.reflect.@PolyDet Type, @PolyDet ReferenceType> entry :
        other.rawMap.entrySet()) {
      @SuppressWarnings(
          "determinism") // The fact the function requiredEntry is @Det is clearly not an issue
      ReferenceType ignore =
          result.rawMap.merge(entry.getKey(), entry.getValue(), requireSameEntry);
    }
    return result;
  }

  /**
   * Returns the concrete type mapped from the type variable by this substitution. Returns null if
   * the variable is not in the substitution.
   *
   * @param parameter the variable
   * @return the concrete type mapped from the variable in this substitution, or null if there is no
   *     type for the variable
   */
  public ReferenceType get(TypeVariable parameter) {
    return map.get(parameter);
  }

  /**
   * Returns the value for the given {@link java.lang.reflect.Type}
   *
   * @param parameter the type variable
   * @return the value for the type variable, or null if there is none
   */
  public ReferenceType get(Type parameter) {
    return rawMap.get(parameter);
  }

  /**
   * Returns the type variables mapped from by this.
   *
   * @return the type variables mapped from by this
   */
  public Set<@PolyDet TypeVariable> keySet() {
    return map.keySet();
  }

  /** Print the entries of this substitution to standard out on multiple lines. */
  public void print(@Det Substitution this) {
    for (Entry<TypeVariable, ReferenceType> entry : map.entrySet()) {
      System.out.println(entry.getKey() + "(" + entry.getKey() + ")" + " := " + entry.getValue());
    }
  }

  /**
   * Add a type variable to concrete type mapping to the substitution.
   *
   * @param typeParameter the type variable
   * @param type the concrete type
   */
  private void put(TypeVariable typeParameter, ReferenceType type) {
    map.put(typeParameter, type);
    if (typeParameter instanceof ExplicitTypeVariable) {
      rawMap.put(((ExplicitTypeVariable) typeParameter).getReflectionTypeVariable(), type);
    }
  }

  /**
   * Indicates whether this substitution is empty.
   *
   * @return true if this has no substitution pairs, false otherwise
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }
}
