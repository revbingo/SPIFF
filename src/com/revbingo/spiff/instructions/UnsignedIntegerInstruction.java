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

import com.revbingo.spiff.ExecutionException;

public class UnsignedIntegerInstruction extends FixedLengthUnsignedNumber {

	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		byte[] bytes = new byte[4];
		int signedInt = buffer.getInt();
		bytes[0] = (byte)(signedInt >> 24);
		bytes[1] = (byte)(signedInt >> 16);
		bytes[2] = (byte)(signedInt >> 8);
		bytes[3] = (byte)(signedInt);

		int[] ubytes = convertBytesToInts(bytes);
		return new Long(ubytes[0] << 24
						| ubytes[1] << 16
						| ubytes[2] << 8
						| ubytes[3]);
	}


}
