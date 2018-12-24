package symbolTable;

import ast.Type.Type;

public abstract class SymbolTableItem {
	protected String name;
	protected Type type;

	public SymbolTableItem() {
	}

	public SymbolTableItem(Type _type) {
		type = _type;
	}

	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public abstract String getKey();
}