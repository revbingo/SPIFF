/* Generated By:JJTree: Do not edit this line. ASTjumpInstruction.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.revbingo.spiff.parser.gen;

public
class ASTjumpInstruction extends SimpleNode {
  public ASTjumpInstruction(int id) {
    super(id);
  }

  public ASTjumpInstruction(SpiffTreeParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SpiffTreeParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=987ddf9f97b9689622296b6ccb5c0c9e (do not edit this line) */
