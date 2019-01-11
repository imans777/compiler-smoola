package ast.node.expression;

import ast.Visitor;

import java.util.ArrayList;

public class MethodCall extends Expression {
    private Expression instance;
    private Identifier methodName;

    public MethodCall(Expression instance, Identifier methodName) {
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

    @Override
    public int getLine() {
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
        return "MethodCall";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
