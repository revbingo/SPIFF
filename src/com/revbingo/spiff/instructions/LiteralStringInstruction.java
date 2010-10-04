package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class LiteralStringInstruction extends StringInstruction {

	private String literal;

	@Override
	byte[] getBytes(ByteBuffer buffer) {
		byte[] bytes = new byte[literal.length()];
		buffer.get(bytes);
		
		String actualString = new String(bytes);
		if(actualString.equals(literal)) {
			return bytes;
		} else {
			throw new ExecutionException("Expected literal string " + literal + " but got " + actualString);
		}
	}
	
	public String getLiteral() {
		return literal;
	}
	
	public void setLiteral(String literal) {
		this.literal = literal;
	}
}
