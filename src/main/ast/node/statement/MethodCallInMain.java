package ast.node.statement;

import ast.Visitor;
import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import java.util.ArrayList;

public class MethodCallInMain extends Statement {
    private Expression instance;
    private Identifier methodName;

    public MethodCallInMain(Expression instance, Identifier methodName) {
        this.instance = instance;
        this.methodName = methodName;
    }

    private ArrayList<Expression> args = new ArrayList<>();

    public Expression getInstance() {
        return instance;
    }

    public void setInstance(Expression instance) {
        this.instance = instance;
    }

    public Identifier getMethodName() {
        return methodName;
    }

    public void setMethodName(Identifier methodName) {
        this.methodName = methodName;
    }

    public ArrayList<Expression> getArgs() {
        return args;
    }

    public void addArg(Expression arg) {
        this.args.add(arg);
    }

    public void setArgs(ArrayList<Expression> args) {
        this.args = args;
    }

    @Override
    public int getLine() { // also sets the line if not already set
        if (line != -1)
            return line;
        else if (instance.getLine() != -1)
            line = instance.getLine();
        else if (methodName.getLine() != -1)
            line = methodName.getLine();
        return line;
    }

    @Override
    public String toString() {
        return "MethodCallInMain";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}