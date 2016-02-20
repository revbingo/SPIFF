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
package com.revbingo.spiff.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class AdfFile {

	private StringBuffer buffer = new StringBuffer();

	public static AdfFile start() {
		return new AdfFile();
	}

	public AdfFile add(String line) {
		return add(line, true);
	}

	public AdfFile add(String line, boolean cr) {
		buffer.append(line);
		if(cr) buffer.append("\n");
		return this;
	}

	public AdfFile end() {
		return this;
	}

	public InputStream asInputStream() {
		try {
			return new ByteArrayInputStream(buffer.toString().getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
