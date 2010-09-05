package com.revbingo.spiff.parser;

import java.util.List;

import com.revbingo.spiff.instructions.Instruction;

public interface InstructionParser {

	public void start() throws ParseException;
	public List<Instruction> getInstructions();
}
