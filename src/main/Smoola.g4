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

    Identifier getID(String text, int line) {
        Identifier id = new Identifier(text);
        id.setLine(line);
        return id;
    }

    Program p = new Program();
}

program:
	mc = mainClass {
        p.setMainClass($mc.cd);
    } (
		oc = classDeclaration {
            p.addClass($oc.cdn);
        }
	)* EOF {
        // final checks and visits
        // can be done in Smoola.java -> not needed for this phase!
    };

mainClass
	returns[ClassDeclaration cd]:
	// name should be checked later
	'class' cname = ID {
        Identifier cnameid = getID($cname.getText(), $cname.getLine());
        Identifier cparentid = new Identifier(null);
        $cd = new ClassDeclaration(cnameid, cparentid);
    } '{' 'def' mmname = ID {
        Identifier mmnameid = getID($mmname.getText(), $mmname.getLine());
        MethodDeclaration mmd = new MethodDeclaration(mmnameid);
    } '(' ')' ':' 'int' '{' sts = statements {
        for (Statement st: $sts.sts) {
            mmd.addStatement(st);
        }
        mmd.setReturnType(new IntType());
    } 'return' r = expression {
        mmd.setReturnValue($r.e);
    } ';' '}' '}' {
        $cd.addMethodDeclaration(mmd);
    };

classDeclaration
	returns[ClassDeclaration cdn]:
	'class' cdname = ID {
        Identifier cdnameid = getID($cdname.getText(), $cdname.getLine());
        Identifier cdparentid = new Identifier(null);
        $cdn = new ClassDeclaration(cdnameid, cdparentid);
        } (
		'extends' parentname = ID {
            Identifier pnameid = getID($parentname.getText(), $parentname.getLine());
            $cdn.setParentName(pnameid);
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
        Identifier vdnameid = getID($vdname.getText(), $vdname.getLine());
        $vd = new VarDeclaration(vdnameid, $vdtype.t);
    } ';';

methodDeclaration
	returns[MethodDeclaration md]:
	'def' methodname = ID {
        Identifier methodnameid = getID($methodname.getText(), $methodname.getLine());
        $md = new MethodDeclaration(methodnameid);
    } (
		'(' ')'
		| (
			'(' argname = ID ':' argtype = type {
        Identifier argnameid = getID($argname.getText(), $argname.getLine()); 
        VarDeclaration vardarg = new VarDeclaration(argnameid, $argtype.t);
        $md.addArg(vardarg);
    } (
				',' argnameo = ID ':' argtypeo = type {
        Identifier argnameoid = getID($argnameo.getText(), $argnameo.getLine());
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
        for(Statement si: $methodst.sts)
            $md.addStatement(si);
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
	stblk = statementBlock {
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
        $stblk = new Block($sts.sts);
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
        $e = $expa.expAssign;
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
	ea = expressionAnd eort = expressionOrTemp[$ea.expAnd] {
        $expOr = $eort.expOrTemp;
    };

expressionOrTemp[Expression pastExpOr]
	returns[Expression expOrTemp]:
	'||' ea = expressionAnd {
        Expression right = $ea.expAnd;
        BinaryExpression be = new BinaryExpression($pastExpOr, right, BinaryOperator.or);
    } eort = expressionOrTemp[be] {
        $expOrTemp = $eort.expOrTemp;
    }
	| {
        $expOrTemp = $pastExpOr;
    };

expressionAnd
	returns[Expression expAnd]:
	ee = expressionEq eat = expressionAndTemp[$ee.expEq] {
        $expAnd = $eat.expAndTemp;
    };

expressionAndTemp[Expression pastExpAnd]
	returns[Expression expAndTemp]:
	'&&' ee = expressionEq {
        Expression right = $ee.expEq;
        BinaryExpression be = new BinaryExpression($pastExpAnd, right, BinaryOperator.and);
    } eat = expressionAndTemp[be] {
        $expAndTemp = $eat.expAndTemp;    
    }
	| {
        $expAndTemp = $pastExpAnd;
    };

expressionEq
	returns[Expression expEq]:
	ec = expressionCmp eet = expressionEqTemp[$ec.expCmp] {
        $expEq = $eet.expEqTemp;
    };

expressionEqTemp[Expression pastExpEq]
	returns[Expression expEqTemp]:
	{
        BinaryOperator bo;
    } (
		'==' { bo = BinaryOperator.eq;
    }
		| '<>' {
            bo = BinaryOperator.neq;
        }
	) ec = expressionCmp {
        Expression right = $ec.expCmp;
        BinaryExpression be = new BinaryExpression($pastExpEq, right, bo);
    } eet = expressionEqTemp[be] {
        $expEqTemp = $eet.expEqTemp;
    }
	| {
        $expEqTemp = $pastExpEq;
    };

expressionCmp
	returns[Expression expCmp]:
	ea = expressionAdd ect = expressionCmpTemp[$ea.expAdd] {
        $expCmp = $ect.expCmpTemp;
    };

expressionCmpTemp[Expression pastExpCmp]
	returns[Expression expCmpTemp]:
	{
        BinaryOperator bo;
    } (
		'<' {
            bo = BinaryOperator.lt;
        }
		| '>' {
            bo = BinaryOperator.gt;
        }
	) ea = expressionAdd {
        Expression right = $ea.expAdd;
        BinaryExpression be = new BinaryExpression($pastExpCmp, right, bo);
    } ect = expressionCmpTemp[be] {
        $expCmpTemp = $ect.expCmpTemp;
    }
	| {
        $expCmpTemp = $pastExpCmp;
    };

expressionAdd
	returns[Expression expAdd]:
	em = expressionMult eat = expressionAddTemp[$em.expMult] {
        $expAdd = $eat.expAddTemp;
    };

expressionAddTemp[Expression pastExpAdd]
	returns[Expression expAddTemp]:
	{
        BinaryOperator bo;
    } (
		'+' {
            bo = BinaryOperator.add;
        }
		| '-' {
            bo =BinaryOperator.sub;
        }
	) em = expressionMult {
        Expression right = $em.expMult;
        BinaryExpression be = new BinaryExpression($pastExpAdd, right, bo);
    } eat = expressionAddTemp[be] {
        $expAddTemp = $eat.expAddTemp;
    }
	| {
        $expAddTemp = $pastExpAdd;
    };

expressionMult
	returns[Expression expMult]:
	eu = expressionUnary emt = expressionMultTemp[$eu.expUn] {
        $expMult = $emt.expMultTemp;
    };

expressionMultTemp[Expression pastExpMult]
	returns[Expression expMultTemp]:
	{
        BinaryOperator bo;
    } (
		'*' {
        bo = BinaryOperator.mult;
    }
		| '/' {
            bo = BinaryOperator.div;
        }
	) eu = expressionUnary {
        Expression right = $eu.expUn;
        BinaryExpression be = new BinaryExpression($pastExpMult, right, bo);
    } emt = expressionMultTemp[be] {
        $expMultTemp = $emt.expMultTemp;
    }
	| {
        $expMultTemp = $pastExpMult;
    };

expressionUnary
	returns[Expression expUn]:
	{
        UnaryOperator uo;
    } (
		'!' {
            uo = UnaryOperator.not;
        }
		| '-' {
            uo = UnaryOperator.minus;
        }
	) eu = expressionUnary {
        $expUn = new UnaryExpression(uo, $eu.expUn);
    }
	| em = expressionMem {
        $expUn = $em.expMem;
    };

expressionMem
	returns[Expression expMem]:
	em = expressionMethods emt = expressionMemTemp[$em.expMethods] {
        $expMem = $emt.expMemTemp;
    };

expressionMemTemp[Expression pastExpMem]
	returns[Expression expMemTemp]:
	'[' e = expression {
        $expMemTemp = new ArrayCall($pastExpMem, $e.e);
     } ']'
	| {
        $expMemTemp = $pastExpMem;
     };

expressionMethods
	returns[Expression expMethods]:
	eo = expressionOther emt = expressionMethodsTemp[$eo.exp] {
        $expMethods = $emt.expMethodsTemp;        
    };

expressionMethodsTemp[Expression pastExpMethods]
	returns[Expression expMethodsTemp]:
	{
        Expression ec = null;
    } '.' (
		id = ID {
            Identifier id = getID($id.getText(), $id.getLine());
            ec = new MethodCall($pastExpMethods, id);
        } '(' ')'
		| id = ID {
            Identifier id = getID($id.getText(), $id.getLine());
            ec = new MethodCall($pastExpMethods, id);
        } '(' (
			ex = expression {
                ((MethodCall)ec).addArg($ex.e);
        } (
				',' ex_repeat = expression {
            ((MethodCall)ec).addArg($ex_repeat.e);
        }
			)*
		) ')'
		| 'length' {
            ec = new Length($pastExpMethods);
        }
	) emt = expressionMethodsTemp[ec] {
        $expMethodsTemp = $emt.expMethodsTemp;
    }
	| {
        $expMethodsTemp = $pastExpMethods;
    };

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