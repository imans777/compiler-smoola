package ast.node.expression;

import ast.Visitor;
import ast.Type.ArrayType.ArrayType;
import ast.node.expression.Value.IntValue;
import ast.node.expression.UnaryExpression;

public class NewArray extends Expression {
    private Expression expression;
    private int line = -1;

    public NewArray() {
        super(new ArrayType());
    }

    public Expression getExpression() {
        return expression;
    }

    public int getInsideNumber() {
        if (expression instanceof IntValue) {
            return ((IntValue) expression).getConstant();
        } else {
            // minus numbers are handled in the grammar
            return 0;
        }
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "NewArray";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
