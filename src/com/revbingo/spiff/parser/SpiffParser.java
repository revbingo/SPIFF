package com.revbingo.spiff.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.gen.ASTadf;
import com.revbingo.spiff.parser.gen.ParseException;
import com.revbingo.spiff.parser.gen.SpiffTreeParser;

public class SpiffParser implements InstructionParser {

	private InputStream input;

	public SpiffParser(InputStream is) {
		input = is;
	}

	@Override
	public List<Instruction> parse() throws ParseException {
		SpiffTreeParser parser = new SpiffTreeParser(input);

		ASTadf rootNode = parser.adf();

		rootNode.dump("");
		SpiffVisitor visitor = new SpiffVisitor();
		List<Instruction> insts = rootNode.jjtAccept(visitor, new ArrayList<Instruction>());
		for(Instruction i : insts) {
			System.out.println(i.getClass().getSimpleName());
		}

		return insts; //parser.getInstructions();
	}

}
