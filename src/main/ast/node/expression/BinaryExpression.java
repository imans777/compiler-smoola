package ast.node.expression;

import ast.Visitor;

public class BinaryExpression extends Expression {

    private Expression left;
    private Expression right;
    private BinaryOperator binaryOperator;

    public BinaryExpression(Expression left, Expression right, BinaryOperator binaryOperator) {
        this.left = left;
        this.right = right;
        this.binaryOperator = binaryOperator;
    }

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public BinaryOperator getBinaryOperator() {
        return binaryOperator;
    }

    public void setBinaryOperator(BinaryOperator binaryOperator) {
        this.binaryOperator = binaryOperator;
    }

    @Override
    public int getLine() { // also sets the line if not already set
        if (line != -1)
            return line;
        else if (left.getLine() != -1)
            line = left.getLine();
        else if (right.getLine() != -1)
            line = right.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "BinaryExpression " + binaryOperator.name();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
