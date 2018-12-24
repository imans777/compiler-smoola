package ast.node.statement;

import ast.Visitor;
import ast.node.expression.Expression;

public class Conditional extends Statement {
    private Expression expression;
    private Statement consequenceBody;
    private Statement alternativeBody;

    public Conditional(Expression expression, Statement consequenceBody) {
        this.expression = expression;
        this.consequenceBody = consequenceBody;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Statement getConsequenceBody() {
        return consequenceBody;
    }

    public void setConsequenceBody(Statement consequenceBody) {
        this.consequenceBody = consequenceBody;
    }

    public Statement getAlternativeBody() {
        return alternativeBody;
    }

    public void setAlternativeBody(Statement alternativeBody) {
        this.alternativeBody = alternativeBody;
    }

    @Override
    public int getLine() { // also sets the line if not already set
        if (line != -1)
            return line;
        else if (expression.getLine() != -1)
            line = expression.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "Conditional";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
