package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

public class DoubleInstruction extends ReferencedInstruction {

	@Override
	public Object evaluate(ByteBuffer buffer) {
		return buffer.getDouble();
	}

}
