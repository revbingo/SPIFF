package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.parser.ParseException;

public class PrintInstruction implements Instruction {

	private String var;

	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher)
			throws ExecutionException {
		System.out.println("PRINT: " + var + " = " + Evaluator.evaluate(var));		
	}

	public void setVar(String s) throws ParseException {
		var = s;
	}

}
