package ast;

import ast.node.Program;
import ast.node.declaration.ClassDeclaration;
import ast.node.declaration.MethodDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.Value.BooleanValue;
import ast.node.expression.Value.IntValue;
import ast.node.expression.Value.StringValue;
import ast.node.statement.*;
import ast.Type.*;
import ast.Type.PrimitiveType.*;
import ast.Type.UserDefinedType.UserDefinedType;
import symbolTable.*;

import java.util.ArrayList;
import java.util.HashMap;

/* PHASE 4 BUGS: 
    -> NOTHING UNRESOLVED ISSUED!!!
*/

public class VisitorImpl implements Visitor {
    private ArrayList<String> preorderLogs = new ArrayList<String>();
    private ArrayList<String> errorLogs = new ArrayList<String>();

    // maps class names to their symbol table
    public HashMap<String, SymbolTable> classST = new HashMap<>();

    // maps the (class, method) key to that method's symbol table
    public HashMap<Key, SymbolTable> methodsST = new HashMap<>();

    // saves the current class name (mostly for handling 'this')
    private String currentClass = "";

    private final String OBJECT_CLASS = "Object";
    private final String TO_STRING_METHOD = "toString";
    private final int MAX_STACK_NUM = 20;
    private ClassDeclaration objectClass;

    public boolean hasError() {
        if (errorLogs.size() > 0) {
            return true;
        }
        return false;
    }

    public void log(String s) {
        preorderLogs.add(s);
    }

    public void err(String s) {
        errorLogs.add(s);
    }

    public void show() {
        if (!hasError()) {
            // NOTE: comment out as it's not useful in phase 4
            // for (String p : preorderLogs)
            // System.out.println(p);
        } else {
            for (String e : errorLogs)
                System.out.println(e);
        }
    }

    // --> Phase 2 Errors
    // ERROR #0 - name not found (in symbolTable)
    // ERROR #1 - check mainClass existence
    // ERROR #2 - check class name redifinition
    // ERROR #3 - variable name redifinition
    // ERROR #4 - check method name redifinition
    // ERROR #5 - array length > 0
    public void produceError(int type, int line, String info) {
        String message = "line:" + line + ":";
        ArrayList<String> errors = new ArrayList<String>();
        errors.add("Item not found " + info);
        errors.add("No class exists in the program");
        errors.add("Redefinition of class " + info);
        errors.add("Redefinition of variable " + info);
        errors.add("Redefinition of method " + info);
        errors.add("Array length should not be zero or negative");

        message += errors.get(type);
        err(message);
    }

    // --> Phase 3 Errors
    // ERROR#40 - this is not supported in main
    // ERROR#31 - variable <variableName> is not declared
    // ERROR#32 - unsupported operand type for <operationName>
    // ERROR#33 - condition type must be boolean
    // ERROR#34 - class <className> is not declared
    // ERROR#35 - there is no method named <methodName> in class <className>
    // ERROR#36 - unsupported type for writeln
    // ERROR#37 - left side of assignment must be a valid lvalue
    // ERROR#38 - <methodName> return type must be <declaratedReturnType>
    // ERROR#49 - method <methodName> arguments does not match with its declaration
    // ERROR#50 - self class inheritence
    // ERROR#51 - circular class inheritence
    // ERROR#52 - array call index is not an integer
    // ERROR#53 - length is not called upon array
    // ERROR#54 - no method call on this object
    // ERROR#55 - array call is not called upon an array
    // ERROR#56 - max stack call reached
    // ERROR#57 - invalid statement
    public void produceError3(int type, int line, String info, String info2) {
        String message = "line:" + line + ":";
        ArrayList<String> errors = new ArrayList<String>();
        errors.add("'this' is not supported in main");
        errors.add("variable " + info + " is not declared");
        errors.add("unsuppored operand type for " + info);
        errors.add("condition type must be boolean");
        errors.add("class " + info + " is not declared");
        errors.add("there is no method named " + info + " in class " + info2);
        errors.add("unsupported type for writeln");
        errors.add("left side of assignment must be a valid lvalue");
        errors.add(info + " return type must be " + info2);
        errors.add("method " + info + " arguments does not match with its declaration");

        errors.add("self class inheritence");
        errors.add("circular class inheritence in " + info);
        errors.add("array call index is not an integer");
        errors.add("length is not called upon an array type object");
        errors.add("no method call available on this object");
        errors.add("array call is not called upon an array");
        errors.add("max stack called reached.");
        errors.add("invalid statement");

        int index = type % 10;
        int set = type / 10;
        if (set == 5)
            message += errors.get(index + 10);
        else
            message += errors.get(index);
        err(message);
    }

    public void produceError3(int type, int line, String info) {
        produceError3(type, line, info, null);
    }

    public void produceError3(int type, int line) {
        produceError3(type, line, null);
    }

    public String binaryOperatorToString(BinaryOperator bop) {
        if (bop == BinaryOperator.add)
            return "add";
        else if (bop == BinaryOperator.sub)
            return "sub";
        else if (bop == BinaryOperator.mult)
            return "mult";
        else if (bop == BinaryOperator.div)
            return "div";
        else if (bop == BinaryOperator.and)
            return "and";
        else if (bop == BinaryOperator.or)
            return "or";
        else if (bop == BinaryOperator.eq)
            return "eq";
        else if (bop == BinaryOperator.neq)
            return "neq";
        else if (bop == BinaryOperator.lt)
            return "lt";
        else if (bop == BinaryOperator.gt)
            return "gt";
        else if (bop == BinaryOperator.assign)
            return "assign";
        return "null";
    }

    public String unaryOperatorToString(UnaryOperator uop) {
        if (uop == UnaryOperator.minus)
            return "minus";
        else if (uop == UnaryOperator.not)
            return "not";
        return "null";
    }

    // visit implemnetations
    @Override
    public void visit(Program program) {
        log(program.toString());
        SymbolTable.push(new SymbolTable());

        // hardcodedly add Object class and toString method
        objectClass = new ClassDeclaration(new Identifier(OBJECT_CLASS), null);
        MethodDeclaration toStringMethod = new MethodDeclaration(new Identifier(TO_STRING_METHOD));
        toStringMethod.setReturnType(new StringType());
        toStringMethod.setReturnValue(new StringValue(OBJECT_CLASS));
        objectClass.addMethodDeclaration(toStringMethod);

        // first just fill classST with empty symbolTables, then overwrite it
        softTraverse(objectClass);
        softTraverse(program.getMainClass());
        for (ClassDeclaration cd : program.getClasses())
            softTraverse(cd);

        // second pass is for setting parent things in the pre symbolTable
        secondPass(objectClass);
        secondPass(program.getMainClass());
        for (ClassDeclaration cd : program.getClasses())
            secondPass(cd);

        // now start traversing
        // objectClass.accept(this);
        program.getMainClass().accept(this);
        for (ClassDeclaration cd : program.getClasses())
            cd.accept(this);
        SymbolTable.pop();
    }

    public void softTraverse(ClassDeclaration cd) {
        SymbolTable temp = new SymbolTable();
        try {
            temp.put(new SymbolTableClassItem(cd.getName().getName(), cd.getParentName(),
                    new UserDefinedType(cd.getName())));
        } catch (ItemAlreadyExistsException iaee) {
        }
        for (VarDeclaration vd : cd.getVarDeclarations())
            softTraverse(vd, temp);
        for (MethodDeclaration md : cd.getMethodDeclarations()) {
            ArrayList<Type> argTypes = new ArrayList<>();
            for (VarDeclaration vd : md.getArgs()) {
                // softTraverse(vd, temp);
                argTypes.add(vd.getType());
            }
            // for (VarDeclaration vd : md.getLocalVars())
            // softTraverse(vd, temp);
            try {
                temp.put(new SymbolTableMethodItem(md.getName().getName(), argTypes, md.getReturnType()));
            } catch (ItemAlreadyExistsException iaee) {
            }
        }

        classST.put(cd.getName().getName(), temp);
    }

    public void softTraverse(VarDeclaration vd, SymbolTable temp) {
        try {
            temp.put(new SymbolTableVariableItem(vd.getIdentifier().getName(), vd.getType(), currentClass));
        } catch (ItemAlreadyExistsException iaee) {
        }
    }

    public void secondPass(ClassDeclaration cd) {
        if (cd.getName().getName() == OBJECT_CLASS)
            return;
        if (cd.getParentName() != null && cd.getParentName().getName() != null) {
            classST.get(cd.getName().getName())
                    .setPreSymbolTable(new SymbolTable(classST.get(cd.getParentName().getName())));
        } else {
            classST.get(cd.getName().getName()).setPreSymbolTable(new SymbolTable(classST.get(OBJECT_CLASS)));
        }
    }

    // --------------------------- Declarations ----------------------------
    @Override
    public void visit(ClassDeclaration classDeclaration, Mode mode) {
        visit(classDeclaration); // mode is not used here, but in VisitorJasmin
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        currentClass = classDeclaration.getName().getName();
        log(classDeclaration.toString());
        try {
            SymbolTable.top.put(new SymbolTableClassItem(classDeclaration.getName().getName(),
                    classDeclaration.getParentName(), new UserDefinedType(classDeclaration.getName())));
        } catch (ItemAlreadyExistsException iaee) {
            produceError(2, classDeclaration.getLine(), classDeclaration.getName().getName());
        }

        boolean hasParent = false;
        if (classDeclaration.getParentName() != null && classDeclaration.getParentName().getName() != null) {
            if (classDeclaration.getName().getName().equals(classDeclaration.getParentName().getName())) {
                produceError3(50, classDeclaration.getName().getLine());
            } else {
                hasParent = true;
                // check circular dependency
                Identifier parName = classDeclaration.getParentName();
                int i = 0;
                while (parName != null) {
                    if (classDeclaration.getName().getName().equals(parName.getName())) {
                        produceError3(51, classDeclaration.getName().getLine(), classDeclaration.getName().getName());
                        hasParent = false;
                        break;
                    }
                    if (i < MAX_STACK_NUM) {
                        i++;
                    } else {
                        produceError3(56, classDeclaration.getName().getLine());
                        hasParent = false;
                        break;
                    }
                    try {
                        parName = ((SymbolTableClassItem) classST.get(parName.getName())
                                .get("class|" + parName.getName())).getParentName();
                    } catch (Exception infe) {
                        parName = null;
                    }
                }
                if (hasParent) {
                    SymbolTable st = classST.get(classDeclaration.getParentName().getName());
                    if (st != null) {
                        SymbolTable.push(new SymbolTable(st));
                    } else {
                        produceError3(34, classDeclaration.getLine(), classDeclaration.getParentName().getName());
                        SymbolTable.push(new SymbolTable());
                    }
                    classDeclaration.getParentName().accept(this, Mode.DECLARE);
                }
            }
        }

        if (!hasParent) {
            // no parent (set parent to "Object" class, if not "Object" itself)
            if (classDeclaration.getName().getName() != OBJECT_CLASS) {
                SymbolTable.push(new SymbolTable(classST.get(OBJECT_CLASS)));
            } else {
                SymbolTable.push(new SymbolTable());
            }
        }

        classDeclaration.getName().accept(this, Mode.DECLARE);
        for (VarDeclaration vd : classDeclaration.getVarDeclarations())
            vd.accept(this, Mode.CLASS);
        for (MethodDeclaration md : classDeclaration.getMethodDeclarations())
            md.accept(this);

        classST.put(classDeclaration.getName().getName(), SymbolTable.top);
        SymbolTable.pop();
        currentClass = "";
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Mode mode) {
        visit(methodDeclaration); // mode is not used here, but in VisitorJasmin
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        log(methodDeclaration.toString());
        ArrayList<Type> argumentTypes = new ArrayList<>();
        for (VarDeclaration v : methodDeclaration.getArgs())
            argumentTypes.add(v.getType());

        try {
            // method existence in parents
            SymbolTable.top.get("method|" + methodDeclaration.getName().getName());
            produceError(4, methodDeclaration.getLine(), methodDeclaration.getName().getName());
        } catch (ItemNotFoundException infe) {
            try {
                SymbolTable.top.put(new SymbolTableMethodItem(methodDeclaration.getName().getName(), argumentTypes,
                        methodDeclaration.getReturnType()));
            } catch (ItemAlreadyExistsException iaee) {
                // method existence in current scope
                produceError(4, methodDeclaration.getLine(), methodDeclaration.getName().getName());
            }
        }

        SymbolTable.push(new SymbolTable(SymbolTable.top)); // set class body to the 'pre' of method's SymbolTable
        SymbolTableVariableItem.resetIndexController(); // reset index for each method
        methodDeclaration.getName().accept(this, Mode.DECLARE);
        for (VarDeclaration arg : methodDeclaration.getArgs())
            arg.accept(this, Mode.METHOD);
        for (VarDeclaration vd : methodDeclaration.getLocalVars())
            vd.accept(this, Mode.METHOD);

        for (Statement st : methodDeclaration.getBody()) {
            try {
                st.accept(this);
            } catch (NullPointerException npe) {
                produceError3(57, -1);
            }
        }

        Expression returnValue = methodDeclaration.getReturnValue();
        returnValue.accept(this);
        if (returnValue.getType() != null) {
            if (!returnValue.getType().toString().equals(methodDeclaration.getReturnType().toString())) {
                produceError3(38, returnValue.getLine(), methodDeclaration.getName().getName(),
                        methodDeclaration.getReturnType().toString());
            }
        }

        methodsST.put(new Key(currentClass, methodDeclaration.getName().getName()), SymbolTable.top);
        SymbolTable.pop();
    }

    @Override
    public void visit(VarDeclaration varDeclaration, Mode mode) {
        log(varDeclaration.toString());
        try {
            if (mode == Mode.CLASS)
                SymbolTable.top.get("var|" + varDeclaration.getIdentifier().getName());
            else if (mode == Mode.METHOD)
                SymbolTable.top.getInCurrentScope("var|" + varDeclaration.getIdentifier().getName());
            produceError(3, varDeclaration.getIdentifier().getLine(), varDeclaration.getIdentifier().getName());
        } catch (ItemNotFoundException infe) {
            try {
                SymbolTable.top.put(new SymbolTableVariableItem(varDeclaration.getIdentifier().getName(),
                        varDeclaration.getType(), currentClass));
            } catch (ItemAlreadyExistsException iaee) {
                // variable existence in scope
                produceError(3, varDeclaration.getIdentifier().getLine(), varDeclaration.getIdentifier().getName());
            }
        }
        varDeclaration.getIdentifier().accept(this, Mode.DECLARE);
    }

    // -------------------------------- Expressions --------------------------------
    @Override
    public void visit(ArrayCall arrayCall) {
        log(arrayCall.toString());
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);

        try {
            int line = arrayCall.getLine();
            if (arrayCall.getIndex().getType().toString() != "int") {
                produceError3(52, line);
            }
            if (arrayCall.getInstance().getType().toString() != "int[]") {
                produceError3(55, line);
            }
        } catch (NullPointerException npe) {
            // errorLog is already issued
        }

    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        log(binaryExpression.toString());

        Expression left = binaryExpression.getLeft();
        Expression right = binaryExpression.getRight();
        left.accept(this);
        right.accept(this);

        int line = binaryExpression.getLine();
        BinaryOperator bop = binaryExpression.getBinaryOperator();
        if (bop == BinaryOperator.add || bop == BinaryOperator.sub || bop == BinaryOperator.mult
                || bop == BinaryOperator.div || bop == BinaryOperator.lt || bop == BinaryOperator.gt) {
            if (left.getType().toString() != "int" || right.getType().toString() != "int") {
                produceError3(32, line, binaryOperatorToString(binaryExpression.getBinaryOperator()));
            }
            binaryExpression.setType(new IntType());
            if (bop == BinaryOperator.lt || bop == BinaryOperator.gt)
                binaryExpression.setType(new BooleanType());
        } else if (bop == BinaryOperator.eq || bop == BinaryOperator.neq) {
            String leftType = left.getType().toString();
            String rightType = right.getType().toString();
            if (leftType != rightType) {
                produceError3(32, line, binaryOperatorToString(binaryExpression.getBinaryOperator()));
            } else if (leftType != "int" && leftType != "string" && leftType != "bool" && leftType != "int[]") {
                produceError3(32, line, binaryOperatorToString(binaryExpression.getBinaryOperator()));
            }
            binaryExpression.setType(new BooleanType());
        } else if (bop == BinaryOperator.and || bop == BinaryOperator.or) {
            if (left.getType().toString() != "bool" || right.getType().toString() != "bool") {
                produceError3(32, line, binaryOperatorToString(binaryExpression.getBinaryOperator()));
            }
            binaryExpression.setType(new BooleanType());
        } else {
            if (bop == BinaryOperator.assign) {
                // after the first '=' in a multi-assignment statemnet, other '='s are counted
                // as BinaryExpression!
                handleAssignment(left, right, line);
                binaryExpression.setType(left.getType());
            } else {
                System.out.println("should not come here!!");
            }
        }
    }

    @Override
    public void visit(Identifier identifier) {
        visit(identifier, Mode.USE);
    }

    @Override
    public void visit(Identifier identifier, Mode mode) {
        log(identifier.toString());

        if (mode == Mode.DECLARE)
            return;

        try {
            SymbolTableItem stiv = SymbolTable.top.get("var|" + identifier.getName());
            identifier.setType(stiv.getType());
        } catch (ItemNotFoundException infe) {
            try {
                SymbolTableItem stim = SymbolTable.top.get("method|" + identifier.getName());
                identifier.setType(stim.getType());
            } catch (ItemNotFoundException infe2) {
                try {
                    SymbolTableItem stic = classST.get(identifier.getName()).get("class|" + identifier.getName());
                    identifier.setType(stic.getType());
                } catch (ItemNotFoundException infe3) {
                    // produceError(0, identifier.getLine(), identifier.getName());
                } catch (NullPointerException npe) {
                    produceError3(31, identifier.getLine(), identifier.getName());
                }
            }
        }
    }

    @Override
    public void visit(Length length) {
        log(length.toString());
        length.getExpression().accept(this);

        int line = length.getLine();
        try {
            if (length.getExpression().getType().toString() != "int[]") {
                produceError3(53, line);
            }
        } catch (NullPointerException npe) {
            produceError3(53, line);
        }
    }

    @Override
    public void visit(MethodCall methodCall) {
        log(methodCall.toString());
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this, Mode.DECLARE);
        for (Expression arg : methodCall.getArgs())
            arg.accept(this);

        int line = methodCall.getLine();
        methodCallErrorCheckings(methodCall, null);
    }

    public void methodCallErrorCheckings(MethodCall mc, MethodCallInMain mcim) {
        Expression inst;
        Identifier methodId;
        ArrayList<Expression> args;
        int line;

        if (mc != null) {
            inst = mc.getInstance();
            methodId = mc.getMethodName();
            args = mc.getArgs();
            line = mc.getLine();
        } else {
            inst = mcim.getInstance();
            methodId = mcim.getMethodName();
            args = mcim.getArgs();
            line = mcim.getLine();
        }

        String className = "";
        if (inst instanceof This) {
            // look in current symbolTable to have that method
            className = currentClass;
        } else if (inst instanceof NewClass) {
            // look for that class symbolTable to have that method
            className = ((UserDefinedType) inst.getType()).getName().getName();
        } else if (inst instanceof Identifier) {
            // look for that variable to be of type class, the class exists, and method
            // exists on that class
            try {
                SymbolTableItem sti = SymbolTable.top.get("var|" + ((Identifier) inst).getName());
                if (((UserDefinedType) (((SymbolTableVariableItem) sti).getTempClassReference())) != null) {
                    className = ((UserDefinedType) ((SymbolTableVariableItem) sti).getTempClassReference()).getName()
                            .getName();
                } else {
                    className = ((UserDefinedType) ((SymbolTableVariableItem) sti).getType()).getName().getName();
                }
            } catch (ItemNotFoundException infe) {
                produceError3(34, line, ((Identifier) inst).getName());
            }
        } else {
            produceError3(54, line);
            return;
        }

        SymbolTable st = classST.get(className);
        if (st == null) {
            produceError3(34, line, className);
        } else {
            // lookup the method
            String methodName = methodId.getName();
            try {
                SymbolTableItem sti = st.get("method|" + methodName);
                if (mc != null)
                    mc.setType(sti.getType());
                else if (mcim != null)
                    mcim.setType(sti.getType());
                // check argument types
                ArrayList<Type> arg = ((SymbolTableMethodItem) sti).getArgTypes();
                ArrayList<Expression> argCall = args;
                if (arg.size() != argCall.size()) {
                    produceError3(49, line, methodName);
                } else {
                    try {
                        for (int i = 0; i < arg.size(); i++) {
                            if (arg.get(i).toString() != argCall.get(i).getType().toString()) {
                                produceError3(49, line, methodName);
                                break;
                            }
                        }
                    } catch (IndexOutOfBoundsException ioobe) {
                        produceError3(49, line, methodName);
                    }
                }
            } catch (ItemNotFoundException infe) {
                produceError3(35, line, methodName, className);
            }
        }
    }

    @Override
    public void visit(NewArray newArray) {
        log(newArray.toString());
        int length = newArray.getInsideNumber();
        if (length <= 0) {
            produceError(5, newArray.getLine(), "");
            ((IntValue) newArray.getExpression()).setConstant(0);
        }
        newArray.getExpression().accept(this);
    }

    @Override
    public void visit(NewClass newClass) {
        log(newClass.toString());
        newClass.getClassName().accept(this);

        int line = newClass.getLine();
        newClass.setType(new UserDefinedType(new Identifier(newClass.getClassName().getName())));
    }

    @Override
    public void visit(This instance) {
        log(instance.toString());
        instance.setType(new UserDefinedType(new Identifier(currentClass)));
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        log(unaryExpression.toString());
        unaryExpression.getValue().accept(this);

        int line = unaryExpression.getLine();
        if (unaryExpression.getUnaryOperator() == UnaryOperator.minus) {
            if (unaryExpression.getValue().getType().toString() != "int") {
                produceError3(32, line, unaryOperatorToString(unaryExpression.getUnaryOperator()));
            }
            unaryExpression.setType(new IntType());
        } else if (unaryExpression.getUnaryOperator() == UnaryOperator.not) {
            if (unaryExpression.getValue().getType().toString() != "bool") {
                produceError3(32, line, unaryOperatorToString(unaryExpression.getUnaryOperator()));
            }
            unaryExpression.setType(new BooleanType());
        } else {
            System.out.println("should not ever come here!");
        }
    }

    @Override
    public void visit(BooleanValue value) {
        log(value.toString());
    }

    @Override
    public void visit(IntValue value) {
        log(value.toString());
    }

    @Override
    public void visit(StringValue value) {
        log(value.toString());
    }

    // -------------------------------- Statements --------------------------------
    @Override
    public void visit(Assign assign) {
        log(assign.toString());
        Expression left = assign.getlValue();
        Expression right = assign.getrValue();
        left.accept(this);
        right.accept(this);
        int line = assign.getLine();
        handleAssignment(left, right, line);
    }

    public void handleAssignment(Expression left, Expression right, int line) {
        try {
            if (!(left instanceof Identifier)) {
                if (!(left instanceof ArrayCall))
                    produceError3(37, line);
            } else {
                try {
                    SymbolTable.top.get("var|" + ((Identifier) left).getName());
                } catch (ItemNotFoundException infe) {
                    produceError3(37, line);
                }
            }

            if (left.getType() instanceof UserDefinedType && right.getType() instanceof UserDefinedType) {
                UserDefinedType ltype = (UserDefinedType) (left.getType());
                UserDefinedType rtype = (UserDefinedType) (right.getType());
                int i = 0;
                while (!ltype.toString().equals(rtype.toString())) {
                    if (i < MAX_STACK_NUM) {
                        i++;
                    } else {
                        produceError3(56, line);
                        break;
                    }
                    SymbolTableItem sti;
                    try {
                        String rname = rtype.getName().getName();
                        sti = classST.get(rname).get("class|" + rname);
                    } catch (ItemNotFoundException infe) {
                        produceError3(32, line, binaryOperatorToString(BinaryOperator.assign));
                        return;
                    }
                    Identifier parentName = ((SymbolTableClassItem) sti).getParentName();
                    if (parentName == null) {
                        produceError3(32, line, binaryOperatorToString(BinaryOperator.assign));
                        return;
                    }
                    rtype = new UserDefinedType(parentName);
                }
                try {
                    ((SymbolTableVariableItem) (SymbolTable.top.get("var|" + ((Identifier) left).getName())))
                            .setTempClassReference(right.getType());
                } catch (ItemNotFoundException infe) {
                }
            } else if (left.getType() instanceof UserDefinedType || right.getType() instanceof UserDefinedType) {
                produceError3(32, line, binaryOperatorToString(BinaryOperator.assign));
            } else {
                if (!left.getType().toString().equals(right.getType().toString())) {
                    produceError3(32, line, binaryOperatorToString(BinaryOperator.assign));
                }
            }
        } catch (NullPointerException npe) {
            produceError3(32, line, binaryOperatorToString(BinaryOperator.assign));
        }
    }

    public void handleAssignment(Expression left, Expression right) {
        handleAssignment(left, right, left.getLine());
    }

    @Override
    public void visit(Block block) {
        log(block.toString());
        for (Statement st : block.getBody())
            st.accept(this);
    }

    @Override
    public void visit(Conditional conditional) {
        log(conditional.toString());
        conditional.getExpression().accept(this);
        conditional.getConsequenceBody().accept(this);
        if (conditional.getAlternativeBody() != null)
            conditional.getAlternativeBody().accept(this);

        int line = conditional.getLine();
        if (conditional.getExpression().getType().toString() != "bool") {
            produceError3(33, line);
        }
    }

    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        log(methodCallInMain.toString());
        methodCallInMain.getInstance().accept(this);
        methodCallInMain.getMethodName().accept(this, Mode.DECLARE);
        for (Expression arg : methodCallInMain.getArgs())
            arg.accept(this);

        int line = methodCallInMain.getLine();
        methodCallErrorCheckings(null, methodCallInMain);
    }

    @Override
    public void visit(While loop) {
        log(loop.toString());
        loop.getCondition().accept(this);
        loop.getBody().accept(this);

        int line = loop.getLine();
        if (loop.getCondition().getType().toString() != "bool") {
            produceError3(33, line);
        }
    }

    @Override
    public void visit(Write write) {
        log(write.toString());

        Expression arg = write.getArg();
        arg.accept(this);

        Type type = arg.getType();
        int line = arg.getLine();
        if (type != null) {
            if (type.toString() != "int" && type.toString() != "string") {
                produceError3(36, line);
            }
        } else {
            // System.out.println("should not come here!!!");
        }
    }
}
