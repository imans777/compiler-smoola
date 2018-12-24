package ast.node.expression.Value;

import ast.Type.Type;
import ast.Type.PrimitiveType.StringType;
import ast.Visitor;

public class StringValue extends Value {
    private String constant = "";

    public StringValue(String constant) {
        super(new StringType());
        this.constant = constant;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    @Override
    public String toString() {
        return "StringValue " + constant;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
