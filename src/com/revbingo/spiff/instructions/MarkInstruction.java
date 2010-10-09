package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public class MarkInstruction implements Instruction {

	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher)
			throws ExecutionException {
		Evaluator.addVariable(name, buffer.position());
		Evaluator.addVariable(name + ".address", buffer.position());
	}

}
