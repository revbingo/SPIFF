/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.revbingo.spiff.ExecutionException;

public abstract class StringInstruction extends ReferencedInstruction {

	protected Charset encoding;

	public StringInstruction(String charsetName) {
		setEncoding(charsetName);
	}

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		byte[] bytes = getBytes(buffer);
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

	abstract byte[] getBytes(ByteBuffer buffer);

}
