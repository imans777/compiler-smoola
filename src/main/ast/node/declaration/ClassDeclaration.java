package ast.node.declaration;

import ast.Visitor;
import ast.Type.Mode;
import ast.node.expression.Identifier;

import java.util.ArrayList;

public class ClassDeclaration extends Declaration {
    public final String OBJECT = "java/lang/Object";
    private Identifier name;
    private Identifier parentName;
    private ArrayList<VarDeclaration> varDeclarations = new ArrayList<>();
    private ArrayList<MethodDeclaration> methodDeclarations = new ArrayList<>();

    public ClassDeclaration(Identifier name, Identifier parentName) {
        this.name = name;
        this.parentName = parentName;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public Identifier getParentName() {
        return parentName;
    }

    public String getParentJasminCode() {
        if (this.parentName != null && this.parentName.getName() != "Object" && this.parentName.getName() != null)
            return parentName.getName();
        else
            return OBJECT;
    }

    public void setParentName(Identifier parentName) {
        this.parentName = parentName;
    }

    public ArrayList<VarDeclaration> getVarDeclarations() {
        return varDeclarations;
    }

    public void addVarDeclaration(VarDeclaration varDeclaration) {
        this.varDeclarations.add(varDeclaration);
    }

    public ArrayList<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    public void addMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclarations.add(methodDeclaration);
    }

    public int getLine() {
        return name.getLine();
    }

    @Override
    public String toString() {
        return "ClassDeclaration";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(Visitor visitor, Mode mode) {
        visitor.visit(this, mode);
    }
}
