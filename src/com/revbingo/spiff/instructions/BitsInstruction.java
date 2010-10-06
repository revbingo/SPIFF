package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.BitSet;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class BitsInstruction extends ReferencedInstruction {

	private String numberOfBitsExpression;
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		int numberOfBits = Evaluator.evaluateInt(numberOfBitsExpression);
		
		int bytesToGet = (int) Math.ceil(numberOfBits/8d);
		byte[] bytes = new byte[bytesToGet];

		buffer.get(bytes);
	
		boolean[] result = new boolean[numberOfBits];
		
		for(int i = 0; i < bytesToGet; i++) {
			for(int j = 7; j >= 0; j--) {
				byte b = (byte) (bytes[i] & 0x01);
				if((i*8) + j < numberOfBits) {
					result[(i*8) + j] = (b == 1);
				}
				bytes[i] = (byte) (bytes[i] >> 1);
			}
		}
		return result;
	}

	public void setNumberOfBitsExpr(String expression) {
		numberOfBitsExpression = expression;
	}

}
