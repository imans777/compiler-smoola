package ast.node.expression;

import ast.Visitor;
import ast.Type.PrimitiveType.*;

public class ArrayCall extends Expression {
    private Expression instance;
    private Expression index;

    public ArrayCall(Expression instance, Expression index) {
        super(new IntType());
        this.instance = instance;
        this.index = index;
    }

    public Expression getInstance() {
        return instance;
    }

    public void setInstance(Expression instance) {
        this.instance = instance;
    }

    public Expression getIndex() {
        return index;
    }

    public void setIndex(Expression index) {
        this.index = index;
    }

    @Override
    public int getLine() {
        if (line != -1)
            return line;
        else if (instance.getLine() != -1)
            line = instance.getLine();
        else if (index.getLine() != -1)
            line = index.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "ArrayCall";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
