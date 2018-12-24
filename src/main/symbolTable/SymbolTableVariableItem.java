package symbolTable;

import ast.Type.Type;

public class SymbolTableVariableItem extends SymbolTableItem {

    private int index;

    // this is used for when we are "new"ing a parent type with a child type
    // so the child type will be recorded here so its methods be accessible!
    private Type tempClassReference = null;

    public SymbolTableVariableItem(String name, Type type, int index) {
        super(type);
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setTempClassReference(Type tempClassReference) {
        this.tempClassReference = tempClassReference;
    }

    public Type getTempClassReference() {
        return tempClassReference;
    }

    @Override
    public String getKey() {
        return "var|" + name;
    }

    public int getIndex() {
        return index;
    }


}