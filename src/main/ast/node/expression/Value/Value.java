package ast.node.expression.Value;

import ast.Type.Type;
import ast.Visitor;
import ast.node.expression.Expression;

public abstract class Value extends Expression {
    public Value() {
    }

    public Value(Type type) {
        super(type);
    }
    @Override
    public void accept(Visitor visitor) {}
}