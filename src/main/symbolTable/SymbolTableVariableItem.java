package symbolTable;

import ast.Type.Type;

public class SymbolTableVariableItem extends SymbolTableItem {

    private int index;

    // this is used for when we are "new"ing a parent type with a child type
    // so the child type will be recorded here so its methods be accessible!
    private Type tempClassReference = null;

    // this is used for we are accessing class fields and we need to know
    // which class this field belongs to, for the jasmin field descriptor!
    private String className = "";

    public SymbolTableVariableItem(String name, Type type, int index) {
        this(name, type); // ignore index!
    }

    public SymbolTableVariableItem(String name, Type type, String className) {
        this(name, type);
        this.className = className;
    }

    public SymbolTableVariableItem(String name, Type type) {
        super(type);
        this.name = name;
        this.index = getNewIndex();
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

    public String getJasminCode(Boolean withClass) {
        String res = name + " " + type.getJasminCode();
        if (withClass && className != "")
            res = className + "/" + res;
        return res;
    }

    // //////////// STATIC PART -> FOR INDEXING \\\\\\\\\\\\
    private static int indexController;

    public static int getNewIndex() {
        return indexController++;
    }

    public static void resetIndexController() {
        indexController = 1; // 0 is 'this', so start at 1
    }
    // \\\\\\\\\\\\ END ////////////

    @Override
    public String getKey() {
        return "var|" + name;
    }

    public int getIndex() {
        return index;
    }

}