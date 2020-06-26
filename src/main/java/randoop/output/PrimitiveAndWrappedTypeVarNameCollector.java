package randoop.output;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.PolyDet;

/**
 * Visit every variable declaration. Adds to a set of strings for all the names of variables that
 * are either primitive or wrapped types.
 */
public class PrimitiveAndWrappedTypeVarNameCollector
    extends VoidVisitorAdapter<@PolyDet Set<@PolyDet String>> {
  /**
   * Visit every variable declaration.
   *
   * @param variableNames a set containing the names of all the variables that are of primitive or
   *     wrapped types. It is modified by side effect.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void visit(VariableDeclarationExpr n, Set<@PolyDet String> variableNames) {
    for (VariableDeclarator vd : n.getVariables()) {
      @PolyDet VariableDeclarator tmp = vd;
      Type t = tmp.getType();
      if (t instanceof PrimitiveType
          || (t instanceof ClassOrInterfaceType && ((ClassOrInterfaceType) t).isBoxedType())) {
        variableNames.add(tmp.getName().toString());
      }
    }
  }
}
