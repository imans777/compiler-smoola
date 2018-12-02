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
import ast.Type.UserDefinedType.UserDefinedType;
import symbolTable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VisitorImpl implements Visitor {
    private ArrayList<String> preorderLogs = new ArrayList<String>();
    private ArrayList<String> errorLogs = new ArrayList<String>();
    private int INDEX = 0;
    private HashMap<String, SymbolTable> classST = new HashMap<>();

    // TODO: symbolTables need to be added here!

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
            for (String p : preorderLogs)
                System.out.println(p);
        } else {
            for (String e : errorLogs)
                System.out.println(e);
        }
    }

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

    // visit implemnetations

    @Override
    public void visit(Program program) {
        log(program.toString());
        SymbolTable.push(new SymbolTable());
        program.getMainClass().accept(this);
        for (ClassDeclaration cd : program.getClasses())
            cd.accept(this);
        SymbolTable.pop();
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        log(classDeclaration.toString());
        try {
            SymbolTable.top.put(new SymbolTableVariableItemBase(classDeclaration.getName().getName(),
                    new UserDefinedType(), INDEX++));
        } catch (ItemAlreadyExistsException iaee) {
            produceError(2, classDeclaration.getLine(), classDeclaration.getName().getName());
        }

        if (classDeclaration.getParentName() != null && classDeclaration.getParentName().getName() != null) {
            try {
                // set pre symbolTable to its parent
                SymbolTable.top.get("var|" + classDeclaration.getParentName().getName());
                SymbolTable.push(new SymbolTable(classST.get(classDeclaration.getParentName().getName())));
            } catch (ItemNotFoundException infe) {
                // parent doesn't exist
                produceError(0, classDeclaration.getLine(), classDeclaration.getParentName().getName());
                SymbolTable.push(new SymbolTable());
            }
            classDeclaration.getParentName().accept(this);
        } else {
            // no extension
            SymbolTable.push(new SymbolTable());
        }

        classDeclaration.getName().accept(this);
        for (VarDeclaration vd : classDeclaration.getVarDeclarations())
            vd.accept(this);
        for (MethodDeclaration md : classDeclaration.getMethodDeclarations())
            md.accept(this);

        classST.put(classDeclaration.getName().getName(), SymbolTable.top);
        SymbolTable.pop();
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
                SymbolTable.top.put(new SymbolTableMethodItem(methodDeclaration.getName().getName(), argumentTypes));
            } catch (ItemAlreadyExistsException iaee) {
                // method existence in current scope
                produceError(4, methodDeclaration.getLine(), methodDeclaration.getName().getName());
            }
        }
        
        SymbolTable.push(new SymbolTable());
        methodDeclaration.getName().accept(this);
        for (VarDeclaration arg : methodDeclaration.getArgs())
            arg.accept(this);
        for (VarDeclaration vd : methodDeclaration.getLocalVars())
            vd.accept(this);

        for (Statement st : methodDeclaration.getBody())
            st.accept(this);
        methodDeclaration.getReturnValue().accept(this);
        SymbolTable.pop();
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        log(varDeclaration.toString());
        try {
            // variable existene in parents
            SymbolTable.top.get("var|" + varDeclaration.getIdentifier().getName());
            produceError(3, varDeclaration.getIdentifier().getLine(), varDeclaration.getIdentifier().getName());
        } catch (ItemNotFoundException infe) {
            try {
                SymbolTable.top.put(new SymbolTableVariableItemBase(varDeclaration.getIdentifier().getName(),
                        varDeclaration.getType(), INDEX++));
            } catch (ItemAlreadyExistsException iaee) {
                // variable existence in current scope
                produceError(3, varDeclaration.getIdentifier().getLine(), varDeclaration.getIdentifier().getName());
            }
        }
        varDeclaration.getIdentifier().accept(this);
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        log(arrayCall.toString());
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        log(binaryExpression.toString());
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
    }

    @Override
    public void visit(Identifier identifier) {
        log(identifier.toString());
    }

    @Override
    public void visit(Length length) {
        log(length.toString());
        length.getExpression().accept(this);
    }

    @Override
    public void visit(MethodCall methodCall) {
        log(methodCall.toString());
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs())
            arg.accept(this);
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
    }

    @Override
    public void visit(This instance) {
        log(instance.toString());
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        log(unaryExpression.toString());
        unaryExpression.getValue().accept(this);
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

    @Override
    public void visit(Assign assign) {
        log(assign.toString());
        assign.getlValue().accept(this);
        assign.getrValue().accept(this);
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
        conditional.getAlternativeBody().accept(this);
    }

    @Override
    public void visit(While loop) {
        log(loop.toString());
        loop.getCondition().accept(this);
        loop.getBody().accept(this);
    }

    @Override
    public void visit(Write write) {
        log(write.toString());
        write.getArg().accept(this);
    }
}
