package com.revbingo.spiff.instructions;

import com.revbingo.spiff.parser.ParseException;

public class FixedLengthNumberFactory {
	
	public ReferencedInstruction getInstruction(String type) throws ParseException{
		if("int".equals(type)) return new IntegerInstruction();
		else if("long".equals(type)) return new LongInstruction();
		else if("float".equals(type)) return new FloatInstruction();
		else if("short".equals(type)) return new ShortInstruction();
		else if("double".equals(type)) return new DoubleInstruction();
		else if("byte".equals(type)) return new ByteInstruction();
		else throw new ParseException("Unknown data type");
	}

}
