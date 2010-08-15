package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.parser.ParseException;

public class JumpInstruction implements Instruction {

	private String expression;
		
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		int result = Evaluator.evaluateInt(expression);
		buffer.position(result);
	}	
	
	public void setExpression(String expression) throws ParseException {
		this.expression = expression;
	}
}

