package randoop.reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import randoop.operation.NonreceiverTerm;
import randoop.operation.TypedOperation;
import randoop.sequence.Sequence;
import randoop.sequence.Variable;
import randoop.types.ClassOrInterfaceType;
import randoop.util.ClassFileConstants;
import randoop.util.MultiMap;

/**
 * {@code ClassLiteralExtractor} is a {@link ClassVisitor} that extracts literals from the bytecode
 * of each class visited, adding a sequence for each to a map associating a sequence with a type.
 *
 * @see OperationModel
 */
class ClassLiteralExtractor extends DefaultClassVisitor {

  private MultiMap<@PolyDet ClassOrInterfaceType, @PolyDet Sequence> literalMap;

  ClassLiteralExtractor(
      @PolyDet MultiMap<@PolyDet ClassOrInterfaceType, @PolyDet Sequence> literalMap) {
    this.literalMap = literalMap;
  }

  @Override
  public void visitBefore(@Det ClassLiteralExtractor this, @Det Class<?> c) {
    @Det Collection<ClassFileConstants.@Det ConstantSet> constList =
        Collections.singletonList(ClassFileConstants.getConstants(c.getName()));
    @Det MultiMap<Class<?>, NonreceiverTerm> constantMap = ClassFileConstants.toMap(constList);
    for (Class<?> constantClass : constantMap.keySet()) {
      @Det ClassOrInterfaceType constantType = ClassOrInterfaceType.forClass(constantClass);
      for (@Det NonreceiverTerm term : constantMap.getValues(constantClass)) {
        @Det Sequence seq =
            new Sequence()
                .extend(
                    TypedOperation.createNonreceiverInitialization(term),
                    new ArrayList<Variable>());
        literalMap.add(constantType, seq);
      }
    }
  }
}
