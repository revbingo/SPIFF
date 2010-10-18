package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class BytesInstruction extends ReferencedInstruction {

	private String lengthExpr;

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		int length = Evaluator.evaluateInt(lengthExpr);
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	public void setLengthExpr(String expr) {
		this.lengthExpr = expr;
	}

	public String getLengthExpr() {
		return lengthExpr;
	}

}
