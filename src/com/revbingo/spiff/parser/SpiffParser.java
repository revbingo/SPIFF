package com.revbingo.spiff.parser;

import java.io.InputStream;
import java.util.List;

import com.revbingo.spiff.instructions.Instruction;

public class SpiffParser implements InstructionParser {

	private InputStream input;

	public SpiffParser(InputStream is) {
		input = is;
	}

	@Override
	public List<Instruction> parse() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(input);

		SimpleNode rootNode = parser.adf();
		rootNode.dump("::");
		return parser.getInstructions();
	}

}
