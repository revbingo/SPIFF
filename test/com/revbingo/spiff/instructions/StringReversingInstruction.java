package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.evaluator.Evaluator;

public class StringReversingInstruction extends Datatype {

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		byte[] bytes = new byte[4];
		buffer.get(bytes);
		return new StringBuffer(new String(bytes)).reverse().toString();
	}

}
