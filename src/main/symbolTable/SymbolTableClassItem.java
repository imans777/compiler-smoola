package symbolTable;

import ast.Type.Type;
import ast.node.expression.Identifier;

public class SymbolTableClassItem extends SymbolTableItem {
    private Identifier parentName = null;

    public SymbolTableClassItem(String name, Type type) {
        super(type);
        this.name = name;
    }

    public SymbolTableClassItem(String name, Identifier parentName, Type type) {
        super(type);
        this.name = name;
        this.parentName = parentName;
    }

    public Identifier getParentName() {
        return parentName;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return "class|" + name;
    }
}