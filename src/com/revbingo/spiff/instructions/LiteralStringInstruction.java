package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.revbingo.spiff.ExecutionException;

public class LiteralStringInstruction extends StringInstruction {

	private String literal;

	public LiteralStringInstruction(String charsetName) {
		super(charsetName);
	}

	@Override
	byte[] getBytes(ByteBuffer buffer) {
		byte[] expectedBytes = literal.getBytes(encoding);
		byte[] actualBytes = new byte[expectedBytes.length];
		buffer.get(actualBytes);

		if(Arrays.equals(actualBytes, expectedBytes)) {
			return actualBytes;
		} else {
			throw new ExecutionException("Expected literal string " + literal + " but got " + new String(actualBytes, encoding));
		}
	}

	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}
}
