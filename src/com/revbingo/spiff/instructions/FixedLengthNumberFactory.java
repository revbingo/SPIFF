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
package com.revbingo.spiff.instructions;

import com.revbingo.spiff.datatypes.*;

public class FixedLengthNumberFactory {

	public NumberType getInstruction(String type) {
		if("int".equals(type)) return new IntegerInstruction();
		else if("long".equals(type)) return new LongInstruction();
		else if("float".equals(type)) return new FloatInstruction();
		else if("short".equals(type)) return new ShortInstruction();
		else if("double".equals(type)) return new DoubleInstruction();
		else if("byte".equals(type)) return new ByteInstruction();
		else if("ubyte".equals(type)) return new UnsignedByteInstruction();
		else if("ushort".equals(type)) return new UnsignedShortInstruction();
		else return new UnsignedIntegerInstruction();
	}

}
