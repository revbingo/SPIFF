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
