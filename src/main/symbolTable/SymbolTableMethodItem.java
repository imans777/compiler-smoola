package symbolTable;

import ast.Type.Type;

import java.util.ArrayList;

public class SymbolTableMethodItem extends SymbolTableItem {

    ArrayList<Type> argTypes = new ArrayList<>();

    public SymbolTableMethodItem(String name, ArrayList<Type> argTypes, Type retType) {
        super(retType); // set the type to return type -> Yay!
        this.name = name;
        this.argTypes = argTypes;
    }

    public ArrayList<Type> getArgTypes() {
        return argTypes;
    }

    @Override
    public String getKey() {
        return "method|" + name;
    }
}
