/*******************************************************************************
 * This file is part of SPIFF.
 * 
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.revbingo.spiff.ExecutionException;

public class LiteralStringInstruction extends StringInstruction {

	private String literal;

	public LiteralStringInstruction(String charsetName) {
		super(charsetName);
	}

	@Override
	byte[] getBytes(ByteBuffer buffer) {
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
