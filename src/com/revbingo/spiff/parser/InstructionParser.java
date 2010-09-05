package com.revbingo.spiff.parser;

import java.util.List;

import com.revbingo.spiff.instructions.Instruction;

public interface InstructionParser {

	public List<Instruction> start() throws ParseException;
}
