package com.revbingo.spiff.parser;

import java.util.List;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.instructions.FixedLengthNumberFactory;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.JumpInstruction;
import com.revbingo.spiff.instructions.MarkInstruction;
import com.revbingo.spiff.instructions.SetInstruction;
import com.revbingo.spiff.instructions.SkipInstruction;
import com.revbingo.spiff.parser.gen.ASTadf;
import com.revbingo.spiff.parser.gen.ASTbits;
import com.revbingo.spiff.parser.gen.ASTbytes;
import com.revbingo.spiff.parser.gen.ASTdatatypeDefs;
import com.revbingo.spiff.parser.gen.ASTdefineInstruction;
import com.revbingo.spiff.parser.gen.ASTentry;
import com.revbingo.spiff.parser.gen.ASTexpression;
import com.revbingo.spiff.parser.gen.ASTfixedNumber;
import com.revbingo.spiff.parser.gen.ASTfixedString;
import com.revbingo.spiff.parser.gen.ASTgroupInstruction;
import com.revbingo.spiff.parser.gen.ASTifInstruction;
import com.revbingo.spiff.parser.gen.ASTincludeInstruction;
import com.revbingo.spiff.parser.gen.ASTinstruction;
import com.revbingo.spiff.parser.gen.ASTjumpInstruction;
import com.revbingo.spiff.parser.gen.ASTlist;
import com.revbingo.spiff.parser.gen.ASTliteralString;
import com.revbingo.spiff.parser.gen.ASTmarkInstruction;
import com.revbingo.spiff.parser.gen.ASTrepeatInstruction;
import com.revbingo.spiff.parser.gen.ASTsetEncodingInstruction;
import com.revbingo.spiff.parser.gen.ASTsetInstruction;
import com.revbingo.spiff.parser.gen.ASTsetOrderInstruction;
import com.revbingo.spiff.parser.gen.ASTskipInstruction;
import com.revbingo.spiff.parser.gen.ASTstring;
import com.revbingo.spiff.parser.gen.ASTterminatedString;
import com.revbingo.spiff.parser.gen.Node;
import com.revbingo.spiff.parser.gen.SimpleNode;
import com.revbingo.spiff.parser.gen.SpiffTreeParserVisitor;
import com.revbingo.spiff.parser.gen.Token;

public class SpiffVisitor implements SpiffTreeParserVisitor {

	@Override
	public List<Instruction> visit(SimpleNode node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTadf node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTdatatypeDefs node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTlist node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTentry node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTbits node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTbytes node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTinstruction node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTstring node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTincludeInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTdefineInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTsetEncodingInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTsetInstruction node,
			List<Instruction> data) {

		SetInstruction ins = new SetInstruction();
	    ins.setVarname(node.name);
	    ins.setExpression(getExpr(node.jjtGetChild(0)));
	    data.add(ins);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTifInstruction node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTsetOrderInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTrepeatInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTgroupInstruction node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTjumpInstruction node,
			List<Instruction> data) {
		JumpInstruction inst = new JumpInstruction();
		inst.setExpression(getExpr(node.jjtGetChild(0)));
		data.add(inst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTskipInstruction node,
			List<Instruction> data) {
		SkipInstruction inst = new SkipInstruction();
		inst.setExpression(getExpr(node.jjtGetChild(0)));
		data.add(inst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTmarkInstruction node,
			List<Instruction> data) {
		MarkInstruction inst = new MarkInstruction();
		inst.setName(node.jjtGetLastToken().image);
		data.add(inst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTfixedString node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTliteralString node, List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTterminatedString node,
			List<Instruction> data) {
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTfixedNumber node, List<Instruction> data) {
	    FixedLengthNumberFactory insF = new FixedLengthNumberFactory();
	    Datatype ins = insF.getInstruction(node.type);
	    ins.setName(node.jjtGetLastToken().image);
	    data.add(ins);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTexpression node, List<Instruction> data) {

		return data;
	}

	private String getExpr(Node node) {
		if(!(node instanceof ASTexpression)) throw new AdfFormatException("Not an expression node");
		ASTexpression exprNode = (ASTexpression) node;
		Token t = exprNode.jjtGetFirstToken();
		StringBuffer expression = new StringBuffer();
		do {
			expression.append(t.image);
			t = t.next;
		} while (t != exprNode.jjtGetLastToken().next);
		return expression.toString();
	}
}