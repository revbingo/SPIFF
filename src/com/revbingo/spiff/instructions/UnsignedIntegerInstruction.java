package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedIntegerInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[4];
		int signedInt = buffer.getInt();
		bytes[0] = (byte)(signedInt >> 24);
		bytes[1] = (byte)(signedInt >> 16);
		bytes[2] = (byte)(signedInt >> 8);
		bytes[3] = (byte)(signedInt);

		int[] ubytes = convertBytesToInts(bytes);
		return new Long(ubytes[0] << 24
						| ubytes[1] << 16
						| ubytes[2] << 8
						| ubytes[3]);
	}


}
