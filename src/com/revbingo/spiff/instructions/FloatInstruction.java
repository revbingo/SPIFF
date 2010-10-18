package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

public class FloatInstruction extends ReferencedInstruction {

	@Override
	public Object evaluate(ByteBuffer buffer) {
		return buffer.getFloat();
	}

}
