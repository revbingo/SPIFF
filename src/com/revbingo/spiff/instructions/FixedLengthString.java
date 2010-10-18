/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.evaluator.Evaluator;

public class FixedLengthString extends StringInstruction {

	private String lengthExpr;

	public FixedLengthString(String charsetName) {
		super(charsetName);
	}

	@Override
	public byte[] getBytes(ByteBuffer buffer) {
		int length = ((Number) Evaluator.evaluate(lengthExpr)).intValue();
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return bytes;
	}

	public void setLengthExpr(String s){
		lengthExpr = s;
	}

	public String getLengthExpr() {
		return lengthExpr;
	}
}
