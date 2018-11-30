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

classDeclaration
	returns[ClassDeclaration cdn]:
	'class' cdname = ID {
        Identifier cdnameid = new Identifier($cdname.getText());
        Identifier cdparentid = new Identifier(null);
        $cdn = new ClassDeclaration(cdnameid, cdparentid);
        } (
		'extends' parentname = ID {
            Identifier pnameid = new Identifier($parentname.getText());
            cdn.setParentName(pnameid);
            }
	)? '{' (
		vd = varDeclaration {
            $cdn.addVarDeclaration($vd.vd);
            }
	)* (
		md = methodDeclaration {
            $cdn.addMethodDeclaration($md.md);
    }
	)* '}';

varDeclaration
	returns[VarDeclaration vd]:
	'var' vdname = ID ':' vdtype = type {
        Identifier vdnameid = new Identifier($vdname.getText());
        $vd = new VarDeclaration(vdnameid, $vdtype.t);
    } ';';

methodDeclaration
	returns[MethodDeclaration md]:
	'def' methodname = ID {
        Identifier methodnameid = new Identifier($methodname.getText());
        $md = new MethodDeclaration(methodnameid);
    } (
		'(' ')'
		| (
			'(' argname = ID ':' argtype = type {
        Identifier argnameid = new Identifier($argname.getText()); 
        VarDeclaration vardarg = new VarDeclaration(argnameid, $argtype.t);
        $md.addArg(vardarg);
    } (
				',' argnameo = ID ':' argtypeo = type {
        Identifier argnameoid = new Identifier($argnameo.getText());
        VarDeclaration vardargo = new VarDeclaration(argnameoid, $argtypeo.t);
        $md.addArg(vardargo);
    }
			)* ')'
		)
	) ':' returnType = type {
        $md.setReturnType($returnType.t);
    } '{' (
		vard = varDeclaration {
        $md.addLocalVar($vard.vd);
    }
	)* methodst = statements {
        $md.addStatement($methodst.sts) //it get a statement, not array of statement    
    } 'return' returnVal = expression {
        $md.setReturnValue($returnVal.e);
    } ';' '}';

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
        $st = new Statement();
    } stblk = statementBlock {
        $st = $stblk.stblk;
    }
	| stcond = statementCondition {
        $st = $stcond.stcond;
    }
	| stloop = statementLoop {
        $st = $stloop.stloop;
    }
	| stwr = statementWrite {
        $st = $stwr.stwr;
    }
	| stassign = statementAssignment {
        $st = $stassign.stassign;
    };

statementBlock
	returns[Block stblk]:
	'{' sts = statements {
        $stblk = new Block($sts.sts) 
    } '}';

statementCondition
	returns[Conditional stcond]:
	'if' '(' stcondexp = expression ')' 'then' consequencest = statement {
        $stcond = new Conditional($stcondexp.e, $consequencest.st);
    } (
		'else' alternativest = statement {
        $stcond.setAlternativeBody($alternativest.st);
    }
	)?;

statementLoop
	returns[While stloop]:
	'while' '(' loopcond = expression ')' loopbody = statement {
        $stloop = new While($loopcond.e, $loopbody.st);
    };

statementWrite
	returns[Write stwr]:
	'writeln(' starg = expression {
        $stwr = new Write($starg.e);
    } ')' ';';

statementAssignment
	returns[Assign stassign]: expression ';';

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
        NewArray newArr = new NewArray();
        Type type = new IntType();
        int num = Integer.parseInt($expArrLength.getText());
        Expression inside = new IntValue(num, type);
        ((NewArray)newArr).setLine($expArrLength.getLine());
        ((NewArray)newArr).setExpression(inside);
        $exp = newArr;
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
        ((Identifier)id).setLine($expId.getLine());
        $exp = id;
    }
	| expArrId = ID '[' expArrIdx = expression ']' {
        Expression inst = new Identifier($expArrId.getText());
        ((Identifier)inst).setLine($expArrId.getLine());
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