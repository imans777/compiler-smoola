package ast.node;

import ast.Visitor;
import ast.Type.*;

public abstract class Node {
    public void accept(Visitor visitor) {}

    public void accept(Visitor visitor, Mode mode) {}
}
