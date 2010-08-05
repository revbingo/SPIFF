package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

public class ByteInstruction extends ReferencedInstruction {

	@Override
	public Object evaluate(ByteBuffer buffer) {
		return buffer.get();
	}
}
