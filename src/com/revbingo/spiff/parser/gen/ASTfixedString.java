/* Generated By:JJTree: Do not edit this line. ASTfixedString.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package com.revbingo.spiff.parser.gen;

public
class ASTfixedString extends SimpleNode {
  public ASTfixedString(int id) {
    super(id);
  }

  public ASTfixedString(SpiffTreeParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public java.util.List<com.revbingo.spiff.instructions.Instruction> jjtAccept(SpiffTreeParserVisitor visitor, java.util.List<com.revbingo.spiff.instructions.Instruction> data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=1663757434e3d62745b25362c99f9942 (do not edit this line) */