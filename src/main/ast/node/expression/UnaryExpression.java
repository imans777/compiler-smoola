package ast.node.expression;

import ast.Visitor;

public class UnaryExpression extends Expression {

    private UnaryOperator unaryOperator;
    private Expression value;

    public UnaryExpression(UnaryOperator unaryOperator, Expression value) {
        this.unaryOperator = unaryOperator;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    @Override
    public int getLine() { // also sets the line if not already set
        if (line != -1)
            return line;
        else if (value.getLine() != -1)
            line = value.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "UnaryExpression " + unaryOperator.name();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
