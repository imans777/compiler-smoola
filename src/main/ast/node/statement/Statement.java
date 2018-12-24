package ast.node.statement;

import ast.Visitor;
import ast.node.Node;
import ast.Type.Type;

public class Statement extends Node {
    protected int line = -1;
    protected Type type = null;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Statement";
    }

    @Override
    public void accept(Visitor visitor) {
    }
}
