package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedLongInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		throw new UnsupportedOperationException();
	}

}
