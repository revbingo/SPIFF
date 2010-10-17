package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedShortInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[2];
		short signedShort = buffer.getShort();
		bytes[0] = (byte) ((signedShort >> 8) & 0xFF);
		bytes[1] = (byte) (signedShort & 0xFF);
		int[] ubytes = convertBytesToInts(bytes);
		return (int) (ubytes[0] << 8 | ubytes[1]);
	}

}
