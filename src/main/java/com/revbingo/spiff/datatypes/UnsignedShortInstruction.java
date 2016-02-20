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

public class UnsignedShortInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[2];
		short signedShort = buffer.getShort();
		bytes[0] = (byte) ((signedShort >> 8) & 0xFF);
		bytes[1] = (byte) (signedShort & 0xFF);
		int[] ubytes = convertBytesToInts(bytes);
		int i = (int) (ubytes[0] << 8 | ubytes[1]);
		if(literalExpr != null) {
			if(i != evaluator.evaluateInt(literalExpr)) {
				throw new ExecutionException("Value " + i + " did not match expected value " + literalExpr);
			}
		}
		return i;
	}

}
