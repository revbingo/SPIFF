package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public abstract class AdfInstruction implements Instruction {

	public int lineNumber;
	public boolean isBreakpoint;
	public int branchTo;

	@Override
	public abstract void execute(ByteBuffer buffer, EventListener eventDispatcher,
			Evaluator evaluator) throws ExecutionException;

}
