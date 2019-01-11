package ast.node.declaration;

import ast.Type.*;
import ast.Type.Type;
import ast.Visitor;
import ast.node.expression.Identifier;

public class VarDeclaration extends Declaration {
    private Identifier identifier;
    private Type type;

    public VarDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getJasminCode() {
        return type.getJasminCode();
    }

    @Override
    public String toString() {
        return "VarDeclaration";
    }

    @Override
    public void accept(Visitor visitor, Mode mode) {
        visitor.visit(this, mode);
    }
}
