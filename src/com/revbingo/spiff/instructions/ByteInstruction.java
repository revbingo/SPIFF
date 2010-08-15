package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

public class ByteInstruction extends ReferencedInstruction {

	public ByteInstruction() { super(); }
	
	public ByteInstruction(String name) {
		this.setName(name);
	}
	
	@Override
	public Object evaluate(ByteBuffer buffer) {
		return buffer.get();
	}
}
