package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.parser.ParseException;

public class SetInstruction implements Instruction {

	private String varname;
	private String expression;
	
	public String getVarname() {
		return varname;
	}

	public void setVarname(String varname) {
		this.varname = varname;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expr) throws ParseException {
		this.expression = expr;
	}

	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		Object result = Evaluator.evaluate(expression);
		Evaluator.addVariable(varname, result);
	}
}
