package ast.node.expression;

import ast.Visitor;
import ast.Type.UserDefinedType.UserDefinedType;

public class NewClass extends Expression {
    private Identifier className;

    public NewClass(Identifier className) {
        super(new UserDefinedType(className));
        this.className = className;
    }

    public Identifier getClassName() {
        return className;
    }

    public void setClassName(Identifier className) {
        this.className = className;
    }

    @Override
    public int getLine() {
        if (line != -1)
            return line;
        else if (className.getLine() != -1)
            line = className.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "NewClass";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
