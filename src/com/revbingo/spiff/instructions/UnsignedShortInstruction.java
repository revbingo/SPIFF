package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedShortInstruction extends FixedLengthUnsignedNumber {
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[2];
		buffer.get(bytes);
		int[] ubytes = convertBytesToInts(bytes);
		return (int) (ubytes[1] << 8 | ubytes[0]);
	}

}
