package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventDispatcher;

public class DummyInstruction implements Instruction {

	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		System.out.println("Dummy instruction executing");
	}

}
