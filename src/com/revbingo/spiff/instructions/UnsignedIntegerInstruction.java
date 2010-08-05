package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedIntegerInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[4];
		buffer.get(bytes);
		int[] ubytes = convertBytesToInts(bytes);
		return new Long(ubytes[3] << 24
						| ubytes[2] << 16
						| ubytes[1] << 8
						| ubytes[0]);
	}

	
}
