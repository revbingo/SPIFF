/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.datatypes;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class BitsInstruction extends Datatype {

	private String numberOfBitsExpression;

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		int numberOfBits = evaluator.evaluateInt(numberOfBitsExpression);

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
