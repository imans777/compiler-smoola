package ast.node.expression;

import ast.Visitor;
import ast.Type.PrimitiveType.IntType;

public class Length extends Expression {
    private Expression expression;

    public Length(Expression expression) {
        super(new IntType());
        this.expression = expression;
        this.setLine(expression.getLine());
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public int getLine() {
        if (line != -1)
            return line;
        else if (expression.getLine() != -1)
            line = expression.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "Length";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
