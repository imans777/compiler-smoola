grammar Smoola;

@header {
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
}

@members {
    void print(Object obj) {
        System.out.println(obj);
    }

    Program p = new Program();
}

program:
	mc = mainClass {
        p.setMainClass($mc.cd);
    } (
		oc = classDeclaration {
            // set other class info
        }
	)* EOF {
        // final checks and visits
        // can be done in Smoola.java
    };

mainClass
	returns[ClassDeclaration cd]:
	// name should be checked later
	'class' cname = ID {
        Identifier cnameid = new Identifier($cname.getText());
        Identifier cparentid = new Identifier(null);
        $cd = new ClassDeclaration(cnameid, cparentid);
    } '{' 'def' mmname = ID {
        Identifier mmnameid = new Identifier($mmname.getText());
        MethodDeclaration mmd = new MethodDeclaration(mmnameid);
    } '(' ')' ':' 'int' '{' sts = statements {
        // TODO: uncomment this after finishing Statement grammar parts!
        // for (Statement st: $sts.sts) {
            // mmd.addStatement(st);
        // }
    } 'return' r = expression {
        mmd.setReturnType(new IntType());
        mmd.setReturnValue($r.e);
    } ';' '}' '}' {
        $cd.addMethodDeclaration(mmd);
    };

classDeclaration:
	'class' ID ('extends' ID)? '{' (varDeclaration)* (
		methodDeclaration
	)* '}';

varDeclaration: 'var' ID ':' type ';';

methodDeclaration:
	'def' ID ('(' ')' | ('(' ID ':' type (',' ID ':' type)* ')')) ':' type '{' varDeclaration*
		statements 'return' expression ';' '}';

statements
	returns[ArrayList<Statement> sts]:
	{
        $sts = new ArrayList<Statement>();
    } (
		st = statement {
            $sts.add($st.st);
        }
	)*;

statement
	returns[Statement st]:
	{
        // fill these empty curly braces in correct format
        $st = new Statement();
    } statementBlock {}
	| statementCondition {}
	| statementLoop {}
	| statementWrite {}
	| statementAssignment {};

statementBlock: '{' statements '}';

statementCondition:
	'if' '(' expression ')' 'then' statement ('else' statement)?;

statementLoop: 'while' '(' expression ')' statement;

statementWrite: 'writeln(' expression ')' ';';

statementAssignment: expression ';';

expression
	returns[Expression e]:
	expa = expressionAssignment {
        // $e = $expa.expAssign
    };

expressionAssignment
	returns[Expression expAssign]:
	eor = expressionOr '=' ea = expressionAssignment {
        Expression left = $eor.expOr;
        Expression right = $ea.expAssign;
        $expAssign = new BinaryExpression(left, right, BinaryOperator.assign);
    }
	| eo = expressionOr {
        $expAssign = $eo.expOr;
    };

expressionOr
	returns[Expression expOr]:
	ea = expressionAnd eort = expressionOrTemp {
        
    };

expressionOrTemp: '||' expressionAnd expressionOrTemp |;

expressionAnd: expressionEq expressionAndTemp;

expressionAndTemp: '&&' expressionEq expressionAndTemp |;

expressionEq: expressionCmp expressionEqTemp;

expressionEqTemp:
	('==' | '<>') expressionCmp expressionEqTemp
	|;

expressionCmp: expressionAdd expressionCmpTemp;

expressionCmpTemp:
	('<' | '>') expressionAdd expressionCmpTemp
	|;

expressionAdd: expressionMult expressionAddTemp;

expressionAddTemp:
	('+' | '-') expressionMult expressionAddTemp
	|;

expressionMult: expressionUnary expressionMultTemp;

expressionMultTemp:
	('*' | '/') expressionUnary expressionMultTemp
	|;

expressionUnary: ('!' | '-') expressionUnary | expressionMem;

expressionMem: expressionMethods expressionMemTemp;

expressionMemTemp: '[' expression ']' |;

expressionMethods: expressionOther expressionMethodsTemp;

expressionMethodsTemp:
	'.' (
		ID '(' ')'
		| ID '(' (expression (',' expression)*) ')'
		| 'length'
	) expressionMethodsTemp
	|;

expressionOther
	returns[Expression exp]:
	expNum = CONST_NUM {
        Type type = new IntType();
        int num = Integer.parseInt($expNum.getText());
        $exp = new IntValue(num, type);
    }
	| expStr = CONST_STR {
        Type type = new StringType();
        String str = $expStr.getText();
        $exp = new StringValue(str, type);
    }
	| 'new ' 'int' '[' expArrLength = CONST_NUM ']' {
        $exp = new NewArray();
        Type type = new IntType();
        int num = Integer.parseInt($expArrLength.getText());
        Expression inside = new IntValue(num, type);
        ((NewArray)$exp).setLine($expArrLength.getLine());
        ((NewArray)$exp).setExpression(inside);
    }
	| 'new ' expClassId = ID '(' ')' {
        Identifier id = new Identifier($expClassId.getText());
        id.setLine($expClassId.getLine());
        $exp = new NewClass(id);
    }
	| expThis = 'this' {
        $exp = new This();
    }
	| expT = 'true' {
        Type type = new BooleanType();
        boolean val = true;
        $exp = new BooleanValue(val, type);
    }
	| expF = 'false' {
        Type type = new BooleanType();
        boolean val = false;
        $exp = new BooleanValue(val, type);
    }
	| expId = ID {
        Expression id = new Identifier($expId.getText());
        id.setLine($expId.getLine());
        $exp = new Identifier(id);
    }
	| expArrId = ID '[' expArrIdx = expression ']' {
        Expression inst = new Identifier($expArrId.getText());
        inst.setLine($expArrId.getLine());
        Expression indx = $expArrIdx.e;
        $exp = new ArrayCall(inst, indx);
    }
	| '(' cexp = expression ')' {
        $exp = $cexp.e;
    };

type
	returns[Type t]:
	'int' { $t = new IntType(); }
	| 'boolean' { $t = new BooleanType(); }
	| 'string' { $t = new StringType(); }
	| 'int' '[' ']' { $t = new ArrayType(); }
	| ID { $t = new UserDefinedType(); };

// lexer rules (start with uppercase) can not reference object nor returning sth
CONST_NUM: [0-9]+;

CONST_STR: '"' ~('\r' | '\n' | '"')* '"';

NL: '\r'? '\n' -> skip;

ID: [a-zA-Z_][a-zA-Z0-9_]*;

COMMENT: '#' (~[\r\n])* -> skip;

WS: [ \t] -> skip;