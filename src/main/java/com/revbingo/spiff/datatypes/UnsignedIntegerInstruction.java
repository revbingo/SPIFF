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

public class UnsignedIntegerInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[4];
		int signedInt = buffer.getInt();
		bytes[0] = (byte)(signedInt >> 24);
		bytes[1] = (byte)(signedInt >> 16);
		bytes[2] = (byte)(signedInt >> 8);
		bytes[3] = (byte)(signedInt);

		int[] ubytes = convertBytesToInts(bytes);
		long l =  Long.valueOf(ubytes[0] << 24
				| ubytes[1] << 16
				| ubytes[2] << 8
				| ubytes[3]);
		if(literalExpr != null) {
			if(l != evaluator.evaluateLong(literalExpr)) {
				throw new ExecutionException("Value " + l + " did not match expected value " + literalExpr);
			}
		}
		return l;
	}


}
