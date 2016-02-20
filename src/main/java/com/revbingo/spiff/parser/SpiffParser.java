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

		SpiffVisitor visitor = new SpiffVisitor();
		List<Instruction> insts = rootNode.jjtAccept(visitor, new ArrayList<Instruction>());

		return insts;
	}

}
