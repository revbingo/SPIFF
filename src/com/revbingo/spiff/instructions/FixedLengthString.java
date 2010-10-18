package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.evaluator.Evaluator;

public class FixedLengthString extends StringInstruction {

	private String lengthExpr;

	public FixedLengthString(String charsetName) {
		super(charsetName);
	}

	@Override
	public byte[] getBytes(ByteBuffer buffer) {
		int length = ((Number) Evaluator.evaluate(lengthExpr)).intValue();
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	public void setLengthExpr(String s){
		lengthExpr = s;
	}

	public String getLengthExpr() {
		return lengthExpr;
	}
}
