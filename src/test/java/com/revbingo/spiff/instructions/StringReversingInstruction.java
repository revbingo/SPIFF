package com.revbingo.spiff.instructions;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.evaluator.Evaluator;

import java.nio.ByteBuffer;

public class StringReversingInstruction extends Datatype {

	public StringReversingInstruction(String name) { super(name); }

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		byte[] bytes = new byte[4];
		buffer.get(bytes);
		return new StringBuffer(new String(bytes)).reverse().toString();
	}

}
