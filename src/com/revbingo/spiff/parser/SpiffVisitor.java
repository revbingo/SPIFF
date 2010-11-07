package com.revbingo.spiff.parser;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.datatypes.BitsInstruction;
import com.revbingo.spiff.datatypes.BytesInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.FixedLengthString;
import com.revbingo.spiff.datatypes.LiteralStringInstruction;
import com.revbingo.spiff.datatypes.StringInstruction;
import com.revbingo.spiff.datatypes.TerminatedString;
import com.revbingo.spiff.instructions.EndGroupInstruction;
import com.revbingo.spiff.instructions.FixedLengthNumberFactory;
import com.revbingo.spiff.instructions.GroupInstruction;
import com.revbingo.spiff.instructions.IfBlock;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.JumpInstruction;
import com.revbingo.spiff.instructions.MarkInstruction;
import com.revbingo.spiff.instructions.RepeatBlock;
import com.revbingo.spiff.instructions.SetInstruction;
import com.revbingo.spiff.instructions.SetOrderInstruction;
import com.revbingo.spiff.instructions.SkipInstruction;
import com.revbingo.spiff.parser.gen.ASTadf;
import com.revbingo.spiff.parser.gen.ASTbits;
import com.revbingo.spiff.parser.gen.ASTbytes;
import com.revbingo.spiff.parser.gen.ASTdatatypeDef;
import com.revbingo.spiff.parser.gen.ASTdefineInstruction;
import com.revbingo.spiff.parser.gen.ASTentry;
import com.revbingo.spiff.parser.gen.ASTexpression;
import com.revbingo.spiff.parser.gen.ASTfixedNumber;
import com.revbingo.spiff.parser.gen.ASTgroupInstruction;
import com.revbingo.spiff.parser.gen.ASTifElseBlock;
import com.revbingo.spiff.parser.gen.ASTincludeInstruction;
import com.revbingo.spiff.parser.gen.ASTjumpInstruction;
import com.revbingo.spiff.parser.gen.ASTlist;
import com.revbingo.spiff.parser.gen.ASTmarkInstruction;
import com.revbingo.spiff.parser.gen.ASTrepeatInstruction;
import com.revbingo.spiff.parser.gen.ASTsetEncodingInstruction;
import com.revbingo.spiff.parser.gen.ASTsetInstruction;
import com.revbingo.spiff.parser.gen.ASTsetOrderInstruction;
import com.revbingo.spiff.parser.gen.ASTskipInstruction;
import com.revbingo.spiff.parser.gen.ASTstring;
import com.revbingo.spiff.parser.gen.ASTuserDefinedType;
import com.revbingo.spiff.parser.gen.Node;
import com.revbingo.spiff.parser.gen.SimpleNode;
import com.revbingo.spiff.parser.gen.SpiffTreeParserConstants;
import com.revbingo.spiff.parser.gen.SpiffTreeParserVisitor;
import com.revbingo.spiff.parser.gen.Token;

public class SpiffVisitor implements SpiffTreeParserVisitor {

	private String defaultEncoding = Charset.defaultCharset().displayName();

	private Map <String, List<Instruction>> defines = new HashMap <String, List<Instruction>> ();
	private Map <String, Class<Datatype>> datatypes = new HashMap<String, Class<Datatype>>();

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
	public List<Instruction> visit(ASTdatatypeDef node, List<Instruction> data) {
		String className = findTokenValue(node, SpiffTreeParserConstants.CLASS);
		try {
			Class<Datatype> datatypeClass = (Class<Datatype>) Class.forName(className);
			if(!Datatype.class.isAssignableFrom(datatypeClass)) {
				throw new AdfFormatException("Custom datatype " + datatypeClass + " does not extend com.revbingo.spiff.datatypes.Datatype");
			}
			datatypes.put(findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER), datatypeClass);
		} catch (ClassNotFoundException e) {
			throw new AdfFormatException("Unknown datatype class " + className);
		}
		node.childrenAccept(this, data);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTuserDefinedType node, List<Instruction> data) {
		String typeName = findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER, 1);
		String identifier = findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER, 2);
  		Class<Datatype> userType = datatypes.get(typeName);
  		if(userType == null) {
  			throw new AdfFormatException("Undefined datatype " + typeName);
  		}
  		try {
  			Datatype inst = userType.newInstance();
  			inst.setName(identifier);
  			data.add(inst);
  		} catch (InstantiationException e) {
			throw new AdfFormatException("Custom datatype " + userType.getName() + " does not have a no-args constructor or threw an exception");
		} catch (IllegalAccessException e) {
			throw new AdfFormatException("Custom datatype " + userType.getName() + " does not have a publically accessible no args constructor");
		}
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
		BitsInstruction inst = new BitsInstruction();
		inst.setName(node.jjtGetLastToken().image);
		inst.setNumberOfBitsExpr(getExpr(node.jjtGetChild(0)));

		data.add(inst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTbytes node, List<Instruction> data) {
		BytesInstruction inst = new BytesInstruction();
		inst.setName(node.jjtGetLastToken().image);
		inst.setLengthExpr(getExpr(node.jjtGetChild(0)));

		data.add(inst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTstring node, List<Instruction> data) {
		StringInstruction ins = null;
		String encoding = findTokenValue(node, SpiffTreeParserConstants.ENCODING);
		if(encoding == null) encoding = defaultEncoding;

		switch(node.type) {
			case FIXED:
				ins = new FixedLengthString(encoding);
			    ((FixedLengthString) ins).setLengthExpr(getExpr(node.jjtGetChild(0)));
				break;
			case LITERAL:
			   	ins = new LiteralStringInstruction(encoding);
			   	((LiteralStringInstruction) ins).setLiteral(node.literal);
				break;
			case TERMINATED:
			   	ins = new TerminatedString(encoding);
				break;
		}

		ins.setName(node.jjtGetLastToken().image);
		data.add(ins);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTincludeInstruction node,
			List<Instruction> data) {
	    List<Instruction> include = defines.get(node.jjtGetLastToken().image);
	    if (include == null) {
	    	throw new AdfFormatException("Could not get defined block with name " + node.jjtGetLastToken());
	    }
	    data.addAll(include);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTdefineInstruction node,
			List<Instruction> data) {
		List<Instruction> nestedInstructions = new ArrayList<Instruction>();
		node.childrenAccept(this, nestedInstructions);
		defines.put(findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER), nestedInstructions);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTsetEncodingInstruction node,
			List<Instruction> data) {
		this.defaultEncoding = findTokenValue(node, SpiffTreeParserConstants.ENCODING);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTsetInstruction node,
			List<Instruction> data) {

		SetInstruction ins = new SetInstruction();
	    ins.setVarname(findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER));
	    ins.setExpression(getExpr(node.jjtGetChild(0)));
	    data.add(ins);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTifElseBlock node, List<Instruction> data) {
		IfBlock ins = new IfBlock();

		ins.setIfExpression(getExpr(node.jjtGetChild(0)));

		List<Instruction> ifInsts = node.jjtGetChild(1).jjtAccept(this, new ArrayList<Instruction>());
		ins.setInstructions(ifInsts);

		if(node.jjtGetNumChildren() == 3) {
			List<Instruction> elseInsts = node.jjtGetChild(2).jjtAccept(this, new ArrayList<Instruction>());
			ins.setElseInstructions(elseInsts);
		}
		data.add(ins);

		return data;
	}

//	@Override
//	public List<Instruction> visit(ASTelseInstruction node,
//			List<Instruction> data) {
//
//	}

	@Override
	public List<Instruction> visit(ASTsetOrderInstruction node,
			List<Instruction> data) {

	    SetOrderInstruction ins = new SetOrderInstruction();
	    ByteOrder order = null;
	    String byteOrder = findTokenValue(node, SpiffTreeParserConstants.BYTEORDER);
	    if (byteOrder.equals("LITTLE_ENDIAN")) {
	      order = ByteOrder.LITTLE_ENDIAN;
	    } else {
	      order = ByteOrder.BIG_ENDIAN;
	    }
	    ins.setOrder(order);
	    data.add(ins);

		return data;
	}

	@Override
	public List<Instruction> visit(ASTrepeatInstruction node,
			List<Instruction> data) {

	    RepeatBlock ins = new RepeatBlock();
	    ins.setRepeatCountExpression(getExpr(node.jjtGetChild(0)));
	    List<Instruction> nestedInstructions = new ArrayList<Instruction>();
	    node.childrenAccept(this, nestedInstructions);
	    ins.setInstructions(nestedInstructions);
	    data.add(ins);

		return data;
	}

	@Override
	public List<Instruction> visit(ASTgroupInstruction node,
			List<Instruction> data) {
		String groupName = findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER);
	    data.add(new GroupInstruction(groupName));

	    node.childrenAccept(this, data);

	    data.add(new EndGroupInstruction(groupName));
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
	public List<Instruction> visit(ASTfixedNumber node, List<Instruction> data) {
	    FixedLengthNumberFactory insF = new FixedLengthNumberFactory();
	    Datatype ins = insF.getInstruction(node.jjtGetFirstToken().image);
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
			String tokenText = t.image;
			if(t.kind == SpiffTreeParserConstants.ID_ADDRESS) {
				tokenText = tokenText.substring(1, tokenText.length()) + ".address";
			}
			expression.append(tokenText);
			t = t.next;
		} while (t != exprNode.jjtGetLastToken().next);
		return expression.toString();
	}

	private String findTokenValue(SimpleNode node, int kind) {
		return findTokenValue(node, kind, 1);
	}

	private String findTokenValue(SimpleNode node, int kind, int count) {
		Token t = node.jjtGetFirstToken();
		do {
			if(t.kind == kind && (--count == 0)) return t.image;
			t = t.next;
		} while (t != node.jjtGetLastToken().next);
		return null;
	}

//	  public void optimise() {
//		    List <Instruction> allInsts = flatten(instructions);
//		    for (Instruction i : allInsts) {
//		      if (i instanceof Datatype) {
//		        Datatype ri = (Datatype) i;
//		        if (!evaluator.isReferenced(ri.getName())) {
//		          ri.setReferenced(false);
//		        }
//		      }
//		    }
//		  }
//
//		  public List<Instruction> getInstructions() {
//		  	optimise();
//		  	return instructions;
//		  }
//
//		  private List <Instruction> flatten(List <Instruction> insts) {
//		    List <Instruction> a = new ArrayList <Instruction> ();
//		    for (Instruction i : insts) {
//		      if (i instanceof Block) {
//		        a.addAll(flatten(((Block) i).getInstructions()));
//		      } else {
//		        a.add(i);
//		      }
//		    }
//		    return a;
//		  }

}
