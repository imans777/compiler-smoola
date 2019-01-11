package symbolTable;

import java.util.*;

public class SymbolTable {

	SymbolTable pre;
	HashMap<String, SymbolTableItem> items;

	// Static members region

	public static SymbolTable top;

	private static Stack<SymbolTable> stack = new Stack<SymbolTable>();

	// Use it in pass 1 scope start
	public static void push(SymbolTable symbolTable) {
		if (top != null)
			stack.push(top);
		top = symbolTable;
	}

	// Use it in pass1 scope end
	public static void pop() {
		top = stack.pop();
	}

	// is used to get the index of a variable
	public static int getIndex(String varkey) {
		try {
			SymbolTableItem sti = top.get("var|" + varkey);
			return ((SymbolTableVariableItem) sti).getIndex();
		} catch (ItemNotFoundException infe) { // definitely exists in symbol table! because we've already type-checked it
			System.out.println("WEIRD ERROR: " + infe.toString());
			return -1;
		}
	}

	// is used to get the jasmin code of a specific variable,
	// with or without class field descriptor
	public static String getJasminCode(String varkey, Boolean withClass) {
		try {
			SymbolTableItem sti = top.get("var|" + varkey);
			SymbolTableVariableItem stvi = (SymbolTableVariableItem) sti;
			return stvi.getJasminCode(withClass);
		} catch (ItemNotFoundException infe) { // definitely exists in symbol table! because we've already type-checked it
			System.out.println("WEIRD ERROR: " + infe.toString());
			return null;
		}
	}

	// is used only in methods, so currentScope of the top symbolTable is the method symbolTable,
	// so if no result was found with getInCurrentScope function, it means it's a class field!
	public static Boolean isClassField(String varkey) {
		try {
			SymbolTableItem sti = top.getInCurrentScope("var|" + varkey);
			return false;
		} catch (ItemNotFoundException infe) {
			return true;
		}
	}
	// End of static members region

	public SymbolTable() {
		this(null);
	}

	public SymbolTable(SymbolTable pre) {
		this.pre = pre;
		this.items = new HashMap<String, SymbolTableItem>();
	}

	public void put(SymbolTableItem item) throws ItemAlreadyExistsException {
		if (items.containsKey(item.getKey()))
			throw new ItemAlreadyExistsException();
		items.put(item.getKey(), item);
	}

	public SymbolTableItem getInCurrentScope(String key) throws ItemNotFoundException {
		SymbolTableItem value = items.get(key);
		if (value == null)
			throw new ItemNotFoundException();
		else
			return value;
	}

	public SymbolTableItem get(String key) throws ItemNotFoundException {
		SymbolTableItem value = items.get(key);
		if (value == null && pre != null)
			return pre.get(key);
		else if (value == null)
			throw new ItemNotFoundException();
		else
			return value;
	}

	public SymbolTable getPreSymbolTable() {
		return pre;
	}

	public void setPreSymbolTable(SymbolTable pre) {
		this.pre = pre;
	}
}