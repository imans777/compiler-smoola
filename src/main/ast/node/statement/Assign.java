package ast.node.statement;

import ast.Visitor;
import ast.node.expression.Expression;

public class Assign extends Statement {
    private Expression lValue;
    private Expression rValue;

    public Assign(Expression lValue, Expression rValue) {
        this.lValue = lValue;
        this.rValue = rValue;
    }

    public Expression getlValue() {
        return lValue;
    }

    public void setlValue(Expression lValue) {
        this.lValue = lValue;
    }

    public Expression getrValue() {
        return rValue;
    }

    public void setrValue(Expression rValue) {
        this.rValue = rValue;
    }

    @Override
    public int getLine() { // also sets the line if not already set
        if (line != -1)
            return line;
        else if (lValue.getLine() != -1)
            line = lValue.getLine();
        else if (rValue.getLine() != -1)
            line = rValue.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "Assign";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
