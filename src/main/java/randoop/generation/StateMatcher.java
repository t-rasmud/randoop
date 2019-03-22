package randoop.generation;

import org.checkerframework.checker.determinism.qual.Det;

public interface StateMatcher {

  boolean add(@Det Object object);

  int size();
}
