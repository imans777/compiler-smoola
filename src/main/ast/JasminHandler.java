package ast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ast.Type.ArrayType.*;
import ast.Type.PrimitiveType.*;
import ast.node.declaration.*;
import ast.node.expression.*;
import ast.node.expression.Value.*;
import ast.node.statement.*;
import ast.Type.Type;
import ast.Type.ArrayType.*;
import ast.Type.PrimitiveType.*;
import ast.Type.UserDefinedType.*;

/**
 * JasminHandler Contains information about working with jasmine, setting the
 * instructions, handle labels and indentation, etc.
 * 
 * theoretically, it should not be dependant to any other files!
 */
public class JasminHandler {

  HashMap<String, Runnable> commands = new HashMap<>();
  public String currentClass = "";
  public int currentIndent = 0;
  public int labelNum = 1;
  public final String OUTPUT_DIR = "output";
  public final String EXT = ".j";
  public final String DOTCLASS = ".class";

  public JasminHandler() {
    // commands.put("test", () -> this.iload());
    // commands.put(".class", () -> this.);
  }

  public void delAt(String dir, String ext) {
    for (File f : new File(dir).listFiles())
      if (f.getName().endsWith(ext)) {
        new File(f.getName()).delete();
        System.out.println("-> deleted " + f.getName());
      }
  }

  public void initiate() {
    // delete old files and add output directory if not exists
    try {
      new File(OUTPUT_DIR).mkdirs();
      delAt(OUTPUT_DIR, EXT);
      delAt(OUTPUT_DIR, DOTCLASS);
    } catch (Exception e) {
      System.out.println("Exception in JasminHandler initiation: " + e.toString());
      new File(OUTPUT_DIR).mkdirs();
    }
  }

  public void setCurrentClass(String cc) {
    currentClass = cc;
  }

  public String getCurrentClass() {
    return currentClass;
  }

  public void incIndent() {
    currentIndent++;
  }

  public void decIndent() {
    currentIndent--;
  }

  public String getNewLabel() {
    return "#L_" + Integer.toString(labelNum++);
  }

  public void writeLabel(String label) {
    addLine(label + ":");
  }

  public String getIndent() {
    String res = "";
    for (int i = 0; i < currentIndent; i++)
      res += "\t\t";
    return res;
  }

  public void addDefaultLimit() {
    addLine(".limit stack 16");
    addLine(".limit locals 16");
  }

  public void addLine(String line) {
    try {
      Files.write(Paths.get(OUTPUT_DIR + File.separator + currentClass.concat(EXT)),
          (getIndent() + line + System.lineSeparator()).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException ioe) {
      System.out.println("IO EXCEPTION THROWN: " + ioe.toString());
    }
  }

  public void initiateClass(ClassDeclaration cd) {
    addLine(".source output");
    String name = cd.getName().getName(), parentName = cd.getParentJasminCode();
    addLine(".class public " + name);
    addLine(".super " + parentName);
  }

  public void handleClassField(VarDeclaration vd) {
    String name = vd.getIdentifier().getName(), type = vd.getJasminCode();
    addLine(".field " + name + " " + type);
  }

  public void initiateConstructor(ClassDeclaration cd) {
    String className = cd.getName().getName(), parentName = cd.getParentJasminCode();
    addLine(".method public <init>()V");
    incIndent();
    addDefaultLimit();
    loadThis();
    addLine("invokespecial " + parentName + "/<init>()V");
    for (VarDeclaration vd : cd.getVarDeclarations()) { // write default values of each type to class fields
      Type type = vd.getType();
      if (!(type instanceof BooleanType) && !(type instanceof IntType) && !(type instanceof StringType))
        continue;
      loadThis();
      addEmptyVar(type);
      putField(className + "/" + vd.getIdentifier().getName() + " " + vd.getJasminCode());
    }
    addLine("return");
    decIndent();
    addLine(".end method");
  }

  public void initiateMethod(MethodDeclaration md) {
    String name = md.getName().getName(), args = md.getArgsJasmineCode(), ret = md.getReturnJasmineCode();
    addLine(".method public " + name + "(" + args + ")" + ret);
    incIndent();
    addDefaultLimit();
  }

  public void initiateMethod(String name, String args, String ret) {
    addLine(".method public " + name + "(" + args + ")" + ret);
    incIndent();
    addDefaultLimit();
  }

  public void endMethod() {
    decIndent();
    addLine(".end method");
  }

  public void handleMethodReturn(MethodDeclaration md, Boolean isMain) {
    if (isMain) {
      addLine("pop");
      addLine("return");
      return;
    }

    Type ret = md.getReturnType();
    if (ret instanceof IntType || ret instanceof BooleanType)
      addLine("ireturn");
    else
      addLine("areturn");
  }

  public void handleMethodVarDeclaration(Type type, int index) {
    addEmptyVar(type);
    storeVar(type, index);
  }

  public void handleLength() {
    addLine("arraylength");
  }

  public void loadArray() {
    addLine("iaload");
  }

  public void storeArray() {
    addLine("iastore");
  }

  public void getField(String desc) {
    loadThis();
    addLine("getfield " + desc);
  }

  public void putField(String desc) {
    addLine("putfield " + desc);
  }

  public void invokeVirtual(String desc) {
    addLine("invokevirtual " + desc);
  }

  public void loadThis() {
    addLine("aload_0");
  }

  public void handleNewArray() {
    addLine("newarray int");
  }

  public void handleNewClass(String name) {
    addLine("new " + name);
    addLine("dup");
    addLine("invokespecial " + name + "/<init>()V");
  }

  public void loadVar(Type type, int index) {
    if (type instanceof IntType || type instanceof BooleanType) {
      addLine("iload " + index);
    } else if (type instanceof StringType || type instanceof ArrayType || type instanceof UserDefinedType) {
      addLine("aload " + index);
    } else {
      System.out.println("UNSUPPORTED TYPE!");
    }
  }

  public void storeVar(Type type, int index) {
    if (type instanceof IntType || type instanceof BooleanType) {
      addLine("istore " + index);
    } else if (type instanceof StringType || type instanceof ArrayType || type instanceof UserDefinedType) {
      addLine("astore " + index);
    } else {
      System.out.println("UNSUPPORTED TYPE!");
    }
  }

  public void addEmptyVar(Type type) {
    if (type instanceof IntType || type instanceof BooleanType) {
      addLine("iconst_0");
    } else if (type instanceof StringType) {
      addLine("ldc \"\"");
    }
  }

  public void addConstValue(Expression value) {
    if (value instanceof BooleanValue) {
      addLine("iconst_" + (((BooleanValue) value).isConstant() ? "1" : "0"));
    } else if (value instanceof IntValue) {
      addLine("bipush " + (((IntValue) value).getConstant()));
    } else if (value instanceof StringValue) {
      addLine("ldc " + (((StringValue) value).getConstant()));
    } else {
      System.out.println("should not come here!!!");
    }
  }

  // handle minus operation :|
  public void minusize() {
    addLine("ineg");
  }

  // handle not operation :|
  public void notesize() {
    String l1 = getNewLabel(), l2 = getNewLabel();
    addLine("ifne " + l1);
    addLine("iconst_1");
    addLine("goto " + l2);
    writeLabel(l1);
    addLine("iconst_0");
    writeLabel(l2);
  }

  public void multOp() {
    addLine("imul");
  }

  public void addOp() {
    addLine("iadd");
  }

  public void divOp() {
    addLine("idiv");
  }

  public void subOp() {
    addLine("isub");
  }

  public void orOp(VisitorJasmin v, Expression left, Expression right) {
    String l1 = getNewLabel(), l2 = getNewLabel(), l3 = getNewLabel();
    left.accept(v);
    addLine("ifne " + l2);
    right.accept(v);
    addLine("ifeq " + l1);
    writeLabel(l2);
    addLine("iconst_1");
    addLine("goto " + l3);
    writeLabel(l1);
    addLine("iconst_0");
    writeLabel(l3);
  }

  public void andOp(VisitorJasmin v, Expression left, Expression right) {
    String l1 = getNewLabel(), l2 = getNewLabel();
    left.accept(v);
    addLine("ifeq " + l1);
    right.accept(v);
    addLine("ifeq " + l1);
    addLine("iconst_1");
    addLine("goto " + l2);
    writeLabel(l1);
    addLine("iconst_0");
    writeLabel(l2);
  }

  public void comprOp(BinaryOperator bop) {
    String jumpJasminCode = "";
    if (bop == BinaryOperator.eq)
      jumpJasminCode = "if_icmpne";
    else if (bop == BinaryOperator.neq)
      jumpJasminCode = "if_icmpeq";
    else if (bop == BinaryOperator.lt)
      jumpJasminCode = "if_icmpge";
    else if (bop == BinaryOperator.gt)
      jumpJasminCode = "if_icmple";
    else {
      System.out.println("better not come here!!!");
      return;
    }

    String l1 = getNewLabel(), l2 = getNewLabel();
    addLine(jumpJasminCode + " " + l1);
    addLine("iconst_1");
    addLine("goto " + l2);
    writeLabel(l1);
    addLine("iconst_0");
    writeLabel(l2);
  }

  public void dupOp(Boolean isDouble) {
    if (isDouble)
      addLine("dup_x2");
    else
      addLine("dup");
  }

  public void ifelse(VisitorJasmin v, Conditional c) {
    String l1 = getNewLabel(), l2 = getNewLabel();
    c.getExpression().accept(v);
    addLine("ifeq " + l1);
    c.getConsequenceBody().accept(v);
    addLine("goto " + l2);
    writeLabel(l1);
    if (c.getAlternativeBody() != null)
      c.getAlternativeBody().accept(v);
    writeLabel(l2);
  }

  public void handleWhile(VisitorJasmin v, While w) {
    String l1 = getNewLabel(), l2 = getNewLabel();
    writeLabel(l1);
    w.getCondition().accept(v);
    addLine("ifeq " + l2);
    w.getBody().accept(v);
    addLine("goto " + l1);
    writeLabel(l2);
  }

  public void write(VisitorJasmin v, Write w) {
    Expression value = w.getArg();
    String jc = "";
    if (value.getType() instanceof ArrayType) {
      // TODO: handle array type things!
    } else {
      addLine("getstatic java/lang/System/out Ljava/io/PrintStream;");
      value.accept(v); // Structurely thinking, "accept" should not be placed here, but rather in
                       // VisitorJasmin
      jc = value.getType().getJasminCode();
    }
    if (value instanceof ArrayCall)
      loadArray();
    addLine("invokevirtual java/io/PrintStream/println(" + jc + ")V");
  }
}