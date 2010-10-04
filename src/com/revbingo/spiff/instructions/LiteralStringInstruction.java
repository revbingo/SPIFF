package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class LiteralStringInstruction extends ReferencedInstruction {

	private String literal;
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		byte[] bytes = new byte[literal.length()];
		buffer.get(bytes);
		
		String actualString = new String(bytes);
		if(actualString.equals(literal)) {
			return actualString;
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
