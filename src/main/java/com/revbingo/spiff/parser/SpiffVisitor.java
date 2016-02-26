/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.parser;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.datatypes.*;
import com.revbingo.spiff.instructions.*;
import com.revbingo.spiff.parser.gen.*;

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
		return data;
	}

	@Override
	public List<Instruction> visit(ASTlist node, List<Instruction> data) {
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
  			return decorateAndAdd(node, inst, data);
  		} catch (InstantiationException e) {
			throw new AdfFormatException("Custom datatype " + userType.getName() + " does not have a no-args constructor or threw an exception");
		} catch (IllegalAccessException e) {
			throw new AdfFormatException("Custom datatype " + userType.getName() + " does not have a publically accessible no args constructor");
		}
	}

	@Override
	public List<Instruction> visit(ASTbits node, List<Instruction> data) {
		BitsInstruction inst = new BitsInstruction();
		inst.setName(node.jjtGetLastToken().image);
		inst.setNumberOfBitsExpr(getExpr(node.jjtGetChild(0)));
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTbytes node, List<Instruction> data) {
		BytesInstruction inst = new BytesInstruction();
		inst.setName(node.jjtGetLastToken().image);
		inst.setLengthExpr(getExpr(node.jjtGetChild(0)));

		return decorateAndAdd(node, inst, data);
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

		SetInstruction inst = new SetInstruction();
	    inst.setVarname(findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER));
	    inst.setExpression(getExpr(node.jjtGetChild(0)));
	    return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTifElseBlock node, List<Instruction> data) {
		IfBlock inst = new IfBlock();

		inst.setIfExpression(getExpr(node.jjtGetChild(0)));

		List<Instruction> ifInsts = node.jjtGetChild(1).jjtAccept(this, new ArrayList<Instruction>());
		inst.setInstructions(ifInsts);

		if(node.jjtGetNumChildren() == 3) {
			List<Instruction> elseInsts = node.jjtGetChild(2).jjtAccept(this, new ArrayList<Instruction>());
			inst.setElseInstructions(elseInsts);
		}
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTsetOrderInstruction node,
			List<Instruction> data) {

	    SetOrderInstruction inst = new SetOrderInstruction();
	    ByteOrder order = null;
	    String byteOrder = findTokenValue(node, SpiffTreeParserConstants.BYTEORDER);
	    if (byteOrder.equals("LITTLE_ENDIAN")) {
	      order = ByteOrder.LITTLE_ENDIAN;
	    } else {
	      order = ByteOrder.BIG_ENDIAN;
	    }
	    inst.setOrder(order);
	    return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTrepeatInstruction node,
			List<Instruction> data) {

	    RepeatBlock inst = new RepeatBlock();
	    inst.setRepeatCountExpression(getExpr(node.jjtGetChild(0)));
	    List<Instruction> nestedInstructions = new ArrayList<Instruction>();
	    node.childrenAccept(this, nestedInstructions);
	    inst.setInstructions(nestedInstructions);
	    return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTgroupInstruction node,
			List<Instruction> data) {
		String groupName = findTokenValue(node, SpiffTreeParserConstants.IDENTIFIER);
	    decorateAndAdd(node, new GroupInstruction(groupName), data);
	    node.childrenAccept(this, data);

	    EndGroupInstruction endGroupInst = new EndGroupInstruction(groupName);
	    endGroupInst.setLineNumber(node.jjtGetLastToken().beginLine);
	    data.add(endGroupInst);
		return data;
	}

	@Override
	public List<Instruction> visit(ASTjumpInstruction node,
			List<Instruction> data) {
		JumpInstruction inst = new JumpInstruction();
		inst.setExpression(getExpr(node.jjtGetChild(0)));
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTskipInstruction node,
			List<Instruction> data) {
		SkipInstruction inst = new SkipInstruction();
		inst.setExpression(getExpr(node.jjtGetChild(0)));
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTmarkInstruction node,
			List<Instruction> data) {
		MarkInstruction inst = new MarkInstruction();
		inst.setName(node.jjtGetLastToken().image);
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTfixedNumber node, List<Instruction> data) {
	    FixedLengthNumberFactory insF = new FixedLengthNumberFactory();
	    NumberType inst = insF.getInstruction(node.jjtGetFirstToken().image);
	    if(node.jjtGetNumChildren() == 1) {
	    	inst.setLiteralExpr(getExpr(node.jjtGetChild(0)));
	    }
	    inst.setName(node.jjtGetLastToken().image);
	    return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTexpression node, List<Instruction> data) {
		return data;
	}

	private String getExpr(Node node) {
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

	private List<Instruction> decorateAndAdd(SimpleNode node, Instruction inst, List<Instruction> list) {
		if(inst instanceof AdfInstruction) {
			((AdfInstruction) inst).setLineNumber(node.jjtGetFirstToken().beginLine);
		}
		list.add(inst);
		return list;
	}
	
	@Override
	public List<Instruction> visit(ASTfixedString node, List<Instruction> data) {
		String encoding = findTokenValue(node, SpiffTreeParserConstants.ENCODING);
		if(encoding == null) encoding = defaultEncoding;

		FixedLengthString inst = new FixedLengthString(encoding);
	    inst.setLengthExpr(getExpr(node.jjtGetChild(0)));
	    
	    inst.setName(node.jjtGetLastToken().image);
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTliteralString node, List<Instruction> data) {
		String encoding = findTokenValue(node, SpiffTreeParserConstants.ENCODING);
		if(encoding == null) encoding = defaultEncoding;

		String literal = ((ASTIdentifier) node.jjtGetChild(0)).jjtGetFirstToken().image;
		LiteralStringInstruction inst = new LiteralStringInstruction(literal, encoding);

	    inst.setName(node.jjtGetLastToken().image);
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTterminatedString node, List<Instruction> data) {
		String encoding = findTokenValue(node, SpiffTreeParserConstants.ENCODING);
		if(encoding == null) encoding = defaultEncoding;

		TerminatedString inst = new TerminatedString(encoding);
		
	    inst.setName(node.jjtGetLastToken().image);
		return decorateAndAdd(node, inst, data);
	}

	@Override
	public List<Instruction> visit(ASTIdentifier node, List<Instruction> data) {
		return null;
	}
}
