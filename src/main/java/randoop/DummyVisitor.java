package randoop;

import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.framework.qual.DefaultQualifier;
import randoop.sequence.ExecutableSequence;

/** A visitor that does nothing and adds no checks. */
@DefaultQualifier(Det.class)
public final class DummyVisitor implements ExecutionVisitor {

  @Override
  public void initialize(@Det DummyVisitor this, ExecutableSequence eseq) {
    // do nothing.
  }

  @Override
  public void visitBeforeStatement(@Det DummyVisitor this, ExecutableSequence eseq, int i) {
    // do nothing
  }

  @Override
  public void visitAfterStatement(@Det DummyVisitor this, ExecutableSequence eseq, int i) {
    // do nothing
  }

  @Override
  public void visitAfterSequence(@Det DummyVisitor this, ExecutableSequence eseq) {
    // do nothing
  }
}
