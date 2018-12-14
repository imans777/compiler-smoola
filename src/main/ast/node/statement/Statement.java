package ast.node.statement;

import ast.Visitor;
import ast.node.Node;

public class Statement extends Node {
    private int line = -1;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "Statement";
    }

    @Override
    public void accept(Visitor visitor) {
    }
}
