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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import com.revbingo.spiff.evaluator.Evaluator;

public class TerminatedString extends StringInstruction {

	public TerminatedString(String charsetName) {
		super(charsetName);
	}

	@Override
	public byte[] getBytes(ByteBuffer buffer, Evaluator evaluator) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte nextByte;
		while((nextByte = buffer.get()) != 0x00) {
			baos.write(nextByte);
		}

		return baos.toByteArray();
	}
}
