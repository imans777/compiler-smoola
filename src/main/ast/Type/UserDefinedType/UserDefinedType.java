package ast.Type.UserDefinedType;

import ast.Type.Type;
import ast.node.expression.Identifier;

public class UserDefinedType extends Type {
    public UserDefinedType() {
    }

    public UserDefinedType(Identifier _name) {
        name = _name;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier _name) {
        name = _name;
    }

    private Identifier name;

    @Override
    public String toString() {
        return (name != null ? name.getName() : "");
    }

    @Override
    public String getJasminCode() {
        return "L" + name.getName() + ";";
    }
}
