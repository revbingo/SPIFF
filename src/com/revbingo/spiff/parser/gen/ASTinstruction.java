/* Generated By:JJTree: Do not edit this line. ASTinstruction.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.revbingo.spiff.parser.gen;

public
class ASTinstruction extends SimpleNode {
  public ASTinstruction(int id) {
    super(id);
  }

  public ASTinstruction(SpiffTreeParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public java.util.List<com.revbingo.spiff.instructions.Instruction> jjtAccept(SpiffTreeParserVisitor visitor, java.util.List<com.revbingo.spiff.instructions.Instruction> data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=8c33b8f053e0ff2adba1c072fa03fd1b (do not edit this line) */