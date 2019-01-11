package ast.node.expression.Value;

import ast.Type.PrimitiveType.BooleanType;
import ast.Visitor;

public class BooleanValue extends Value {
    private boolean constant = false;

    public BooleanValue(boolean constant) {
        super(new BooleanType());
        this.constant = constant;
    }

    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "BooleanValue " + constant;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
