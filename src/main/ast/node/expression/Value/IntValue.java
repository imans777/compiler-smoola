package ast.node.expression.Value;

import ast.Type.Type;
import ast.Type.PrimitiveType.IntType;
import ast.Visitor;

public class IntValue extends Value {
    private int constant = 0;

    public IntValue(int constant) {
        super(new IntType());
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }

    public void setConstant(int constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "IntValue " + constant;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
