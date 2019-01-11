package ast.Type;


/**
 * Mode is a general enumration for anything that needs
 * some modifications upon different situations!
 * 
 * Usage:
 * 
 * CLASS, METHOD:
 *  in varDeclaration, it is used for checking whether it is a
 *  variable from class (class property) or variable defined in
 *  the method of a class.
 *  Reason: because we want to set 'pre' symbolTable of the methods
 *  to be their class symbolTable and for the classes, it's their
 *  parent's. So for redefinition, in methods we need to check only
 *  in the method scope, but in class we need to check both in the
 *  class body AND the parents' bodies!
 * 
 * DECLARE, USE:
 *  in Identifier, it is used for checking whether this identifier
 *  is being declared or being used.
 *  Reason: because when we are declaring, we check the existence of
 *  of it in class/method/var Declaration function, and we don't want
 *  to throw error for identifier not existence/already existence twice!
 *  we only produce error when we are in USE mode.
 *  note: in "class a extends b", in both "a" and "b", we are in DECLARE mode!
 *  note: in "new b().func()", in "func", we are in DECLARE mode!
 * 
 * MAIN, NOTMAIN:
 *  this is used in VisitorJasmin, for checking if we are in the main
 *  method of the main class or other methods
 *  Reason: because we need to set "static main" and other initiations
 *  in the jasmin visitor so that we can distinguish between them
 */
public enum Mode {
  CLASS, METHOD,

  DECLARE, USE,

  MAIN, NOTMAIN,


}