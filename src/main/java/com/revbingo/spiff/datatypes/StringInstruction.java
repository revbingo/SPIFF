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
import java.nio.charset.Charset;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public abstract class StringInstruction extends Datatype {

	protected Charset encoding;

	public StringInstruction(String charsetName) {
		setEncoding(charsetName);
	}

	@Override
	public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
		byte[] bytes = getBytes(buffer, evaluator);
		String result = "";
		result = new String(bytes, encoding);
		return result.trim();
	}

	public void setEncoding(String charsetName) {
		try {
			this.encoding = Charset.forName(charsetName);
		} catch(Exception e) {
			throw new ExecutionException("Unknown or unsupported charset :" + charsetName);
		}
	}

	public Charset getEncoding() {
		return encoding;
	}

	abstract byte[] getBytes(ByteBuffer buffer, Evaluator evaluator);

}
