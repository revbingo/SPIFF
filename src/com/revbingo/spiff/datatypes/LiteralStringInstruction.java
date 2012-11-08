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
import java.util.Arrays;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class LiteralStringInstruction extends StringInstruction {

	private String literal;

	public LiteralStringInstruction(String charsetName) {
		super(charsetName);
	}

	@Override
	byte[] getBytes(ByteBuffer buffer, Evaluator evaluator) {
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
