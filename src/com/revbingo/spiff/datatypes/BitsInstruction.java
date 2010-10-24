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

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class BitsInstruction extends Datatype {

	private String numberOfBitsExpression;

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		int numberOfBits = Evaluator.getInstance().evaluateInt(numberOfBitsExpression);

		int bytesToGet = (int) Math.ceil(numberOfBits/8d);
		byte[] bytes = new byte[bytesToGet];

		buffer.get(bytes);

		boolean[] result = new boolean[numberOfBits];

		for(int i = 0; i < bytesToGet; i++) {
			for(int j = 7; j >= 0; j--) {
				byte b = (byte) (bytes[i] & 0x01);
				if((i*8) + j < numberOfBits) {
					result[(i*8) + j] = (b == 1);
				}
				bytes[i] = (byte) (bytes[i] >> 1);
			}
		}
		return result;
	}

	public void setNumberOfBitsExpr(String expression) {
		numberOfBitsExpression = expression;
	}

}
