/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class TerminatedString extends StringInstruction {

	public TerminatedString(String charsetName) {
		super(charsetName);
	}

	@Override
	public byte[] getBytes(ByteBuffer buffer) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte nextByte;
		while((nextByte = buffer.get()) != 0x00) {
			baos.write(nextByte);
		}

		return baos.toByteArray();
	}
}
