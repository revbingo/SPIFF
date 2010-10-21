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

import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.DoubleInstruction;
import com.revbingo.spiff.datatypes.FloatInstruction;
import com.revbingo.spiff.datatypes.IntegerInstruction;
import com.revbingo.spiff.datatypes.LongInstruction;
import com.revbingo.spiff.datatypes.ShortInstruction;
import com.revbingo.spiff.parser.ParseException;

public class FixedLengthNumberFactory {
	
	public Datatype getInstruction(String type) throws ParseException{
		if("int".equals(type)) return new IntegerInstruction();
		else if("long".equals(type)) return new LongInstruction();
		else if("float".equals(type)) return new FloatInstruction();
		else if("short".equals(type)) return new ShortInstruction();
		else if("double".equals(type)) return new DoubleInstruction();
		else if("byte".equals(type)) return new ByteInstruction();
		else throw new ParseException("Unknown data type");
	}

}
