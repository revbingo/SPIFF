package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class IntegerInstruction extends ReferencedInstruction {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		return buffer.getInt();
	}
}
