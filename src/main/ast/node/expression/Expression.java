package ast.node.expression;

import ast.Type.Type;
import ast.Visitor;
import ast.node.Node;

public abstract class Expression extends Node {
    protected Type type = null;
    protected int line = -1;

    public Expression() {
    }

    public Expression(Type _type) {
        type = _type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public void accept(Visitor visitor) {
    }
}