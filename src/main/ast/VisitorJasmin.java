package ast;

import java.nio.file.Files;
import java.nio.file.Paths;

import ast.*;
import ast.node.*;
import ast.node.declaration.*;
import ast.node.expression.*;
import ast.node.expression.Value.*;
import ast.node.statement.*;
import ast.Type.*;
import ast.Type.ArrayType.*;
import ast.Type.PrimitiveType.*;
import ast.Type.UserDefinedType.*;
import symbolTable.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* PHASE 4 NOT IMPLEMENTIES:
    -> writeln an array
    -> in triple assignment, only if the second is class field, it gets error!
    :|
*/

public class VisitorJasmin implements Visitor {
  VisitorImpl vi;
  JasminHandler jh;

  public VisitorJasmin(VisitorImpl vi) {
    this.vi = vi;
    jh = new JasminHandler();
    jh.initiate();
  }

  @Override
  public void visit(Program program) {
    // this is literally the 4th pass (after 3 passes of VisitorImpl)
    // symbolTable information is completely stored in vi.classST

    SymbolTable.push(new SymbolTable());
    program.getMainClass().accept(this, Mode.MAIN);
    for (ClassDeclaration cd : program.getClasses())
      cd.accept(this);
    SymbolTable.pop();
  }

  // --------------------------- Declarations ----------------------------
  @Override
  public void visit(ClassDeclaration classDeclaration) {
    visit(classDeclaration, Mode.NOTMAIN);
  }

  @Override
  public void visit(ClassDeclaration classDeclaration, Mode mode) {
    jh.setCurrentClass(classDeclaration.getName().getName());
    SymbolTable.push(vi.classST.get(jh.getCurrentClass()));

    jh.initiateClass(classDeclaration);

    for (VarDeclaration vd : classDeclaration.getVarDeclarations())
      jh.handleClassField(vd);

    jh.initiateConstructor(classDeclaration);

    for (MethodDeclaration md : classDeclaration.getMethodDeclarations())
      md.accept(this, mode);
    SymbolTable.pop();
  }

  @Override
  public void visit(MethodDeclaration methodDeclaration) {
    visit(methodDeclaration, Mode.NOTMAIN);
  }

  @Override
  public void visit(MethodDeclaration methodDeclaration, Mode mode) {
    SymbolTable.push(vi.methodsST.get(new Key(jh.getCurrentClass(), methodDeclaration.getName().getName())));
    if (mode == Mode.MAIN) {
      jh.initiateMethod("static main", "[Ljava/lang/String;", "V");
    } else {
      jh.initiateMethod(methodDeclaration);
    }
    for (VarDeclaration vd : methodDeclaration.getLocalVars())
      vd.accept(this);
    for (Statement st : methodDeclaration.getBody())
      st.accept(this);

    Expression retVal = methodDeclaration.getReturnValue();
    retVal.accept(this);
    if (retVal instanceof ArrayCall)
      jh.loadArray();
    jh.handleMethodReturn(methodDeclaration, mode == Mode.MAIN);
    jh.endMethod();
    SymbolTable.pop();
  }

  public void visit(VarDeclaration varDeclaration) {
    visit(varDeclaration, Mode.METHOD);
  }

  @Override
  public void visit(VarDeclaration varDeclaration, Mode mode) {
    Type type = varDeclaration.getType();
    int index = SymbolTable.getIndex(varDeclaration.getIdentifier().getName());
    jh.handleMethodVarDeclaration(type, index);
  }

  // -------------------------------- Expressions --------------------------------
  @Override
  public void visit(ArrayCall arrayCall) {
    arrayCall.getInstance().accept(this);
    arrayCall.getIndex().accept(this);
  }

  @Override
  public void visit(BinaryExpression binaryExpression) {
    Expression left = binaryExpression.getLeft();
    Expression right = binaryExpression.getRight();
    BinaryOperator bop = binaryExpression.getBinaryOperator();

    if (bop == BinaryOperator.add) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.addOp();
    } else if (bop == BinaryOperator.sub) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.subOp();
    } else if (bop == BinaryOperator.mult) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.multOp();
    } else if (bop == BinaryOperator.div) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.divOp();
    } else if (bop == BinaryOperator.and) {
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.andOp(this, left, right);
    } else if (bop == BinaryOperator.or) {
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.orOp(this, left, right);
    } else if (bop == BinaryOperator.eq) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.comprOp(bop);
    } else if (bop == BinaryOperator.neq) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.comprOp(bop);
    } else if (bop == BinaryOperator.lt) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.comprOp(bop);
    } else if (bop == BinaryOperator.gt) {
      left.accept(this);
      if (left instanceof ArrayCall)
        jh.loadArray();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      jh.comprOp(bop);
    } else if (bop == BinaryOperator.assign) {
      Boolean isclassfield = false;
      String name = "";
      if (left instanceof ArrayCall) {
        left.accept(this);
        name = ((Identifier) (((ArrayCall) left).getInstance())).getName();
        isclassfield = SymbolTable.isClassField(name);
      } else if (left instanceof Identifier) {
        name = ((Identifier) left).getName();
        isclassfield = SymbolTable.isClassField(name);
      } else {
        System.out.println("should've not come here!");
        return;
      }

      if (isclassfield)
        jh.loadThis();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();

      if (left instanceof ArrayCall)
        jh.dupOp(true);
      else
        jh.dupOp(false);

      if (left instanceof ArrayCall) {
        if (isclassfield)
          jh.putField(SymbolTable.getJasminCode(name, true));
        else
          jh.storeArray();
      } else if (left instanceof Identifier) {
        if (isclassfield)
          jh.putField(SymbolTable.getJasminCode(name, true));
        else
          jh.storeVar(left.getType(), SymbolTable.getIndex(name));
      } else {
        System.out.println("should've not come here!");
      }
    }
  }

  @Override
  public void visit(Identifier identifier) {
    visit(identifier, Mode.USE);
  }

  @Override
  public void visit(Identifier identifier, Mode mode) {
    String name = identifier.getName();
    if (SymbolTable.isClassField(name)) {
      jh.getField(SymbolTable.getJasminCode(name, true));
    } else {
      jh.loadVar(identifier.getType(), SymbolTable.getIndex(name));
    }
  }

  @Override
  public void visit(Length length) {
    length.getExpression().accept(this);
    jh.handleLength();
  }

  @Override
  public void visit(MethodCall methodCall) {
    methodCallHandling(methodCall.getInstance(), methodCall.getMethodName(), methodCall.getArgs());
  }

  public void methodCallHandling(Expression inst, Identifier methodId, ArrayList<Expression> args) {
    inst.accept(this);
    for (Expression arg : args)
      arg.accept(this);

    try {
      String className = "", methodName = "", desc = "";
      className = inst.getType().toString();
      methodName = methodId.getName();
      desc = className + "/" + methodName;
      SymbolTable classST = vi.classST.get(className);
      SymbolTableMethodItem stmi = (SymbolTableMethodItem) classST.get("method|" + methodName);
      desc += "(";
      for (Type arg : stmi.getArgTypes())
        desc += arg.getJasminCode();
      desc += ")";
      desc += stmi.getType().getJasminCode();
      jh.invokeVirtual(desc);
    } catch (ItemNotFoundException infe) {
      System.out.println("WEIRD ERROR: " + infe.toString());
    } catch (Exception e) {
      System.out.println("WEIRD ERROR: " + e.toString());
    }
  }

  @Override
  public void visit(NewArray newArray) {
    newArray.getExpression().accept(this);
    jh.handleNewArray();
  }

  @Override
  public void visit(NewClass newClass) {
    jh.handleNewClass(newClass.getClassName().getName());
  }

  @Override
  public void visit(This instance) {
    jh.loadThis();
  }

  @Override
  public void visit(UnaryExpression unaryExpression) {
    Expression value = unaryExpression.getValue();
    UnaryOperator uo = unaryExpression.getUnaryOperator();

    value.accept(this);
    if (value instanceof ArrayCall)
      jh.loadArray();

    if (uo == UnaryOperator.minus)
      jh.minusize();
    else if (uo == UnaryOperator.not)
      jh.notesize();
    else
      System.out.println("should not ever come here!!!!");
  }

  @Override
  public void visit(BooleanValue value) {
    jh.addConstValue(value);
  }

  @Override
  public void visit(IntValue value) {
    jh.addConstValue(value);
  }

  @Override
  public void visit(StringValue value) {
    jh.addConstValue(value);
  }

  // -------------------------------- Statements --------------------------------
  @Override
  public void visit(Assign assign) {
    Expression left = assign.getlValue();
    Expression right = assign.getrValue();
    if (left instanceof ArrayCall) {
      left.accept(this);
      Identifier inst = (Identifier) (((ArrayCall) left).getInstance());
      Boolean isclassfield = SymbolTable.isClassField(inst.getName());
      if (isclassfield)
        jh.loadThis();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      if (isclassfield)
        jh.putField(SymbolTable.getJasminCode(inst.getName(), true));
      else
        jh.storeArray();
    } else { // if not array, then it's definitely of type Identifier
      String name = ((Identifier) left).getName();
      Boolean isclassfield = SymbolTable.isClassField(name);
      if (isclassfield)
        jh.loadThis();
      right.accept(this);
      if (right instanceof ArrayCall)
        jh.loadArray();
      if (isclassfield)
        jh.putField(SymbolTable.getJasminCode(name, true));
      else
        jh.storeVar(left.getType(), SymbolTable.getIndex(name));
    }
  }

  @Override
  public void visit(Block block) {
    for (Statement st : block.getBody())
      st.accept(this);
  }

  @Override
  public void visit(Conditional conditional) {
    jh.ifelse(this, conditional);
  }

  @Override
  public void visit(MethodCallInMain methodCallInMain) {
    methodCallHandling(methodCallInMain.getInstance(), methodCallInMain.getMethodName(), methodCallInMain.getArgs());
  }

  @Override
  public void visit(While loop) {
    jh.handleWhile(this, loop);
  }

  @Override
  public void visit(Write write) {
    jh.write(this, write);
  }
}