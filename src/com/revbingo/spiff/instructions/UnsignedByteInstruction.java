/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class UnsignedByteInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte [] bytes = new byte[1];
		buffer.get(bytes);
		return (short) convertBytesToInts(bytes)[0];
	}
}
