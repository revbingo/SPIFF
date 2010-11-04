package com.revbingo.spiff.parser;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.revbingo.spiff.instructions.Instruction;

public class SpiffParser implements InstructionParser {

	private InputStream input;
	private Reader reader;

	public SpiffParser(InputStream is) {
		input = is;
	}

	public SpiffParser(Reader r) {
		reader = r;
	}

	@Override
	public List<Instruction> parse() throws ParseException {
		SpiffTreeParser parser;
		if(input != null) {
			parser = new SpiffTreeParser(input);
		} else {
			parser = new SpiffTreeParser(reader);
		}
		parser.parse();
		return parser.getInstructions();
	}

}
