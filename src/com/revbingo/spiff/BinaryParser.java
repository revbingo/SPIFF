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
package com.revbingo.spiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.InstructionParser;
import com.revbingo.spiff.parser.SpiffParser;
import com.revbingo.spiff.parser.gen.ParseException;
import com.revbingo.spiff.vm.SpiffVm;

public class BinaryParser {

	private EventListener eventDispatcher;

	public BinaryParser(EventListener ed){
		this.eventDispatcher = ed;
	}

	public void parse(File adfFile, File parseFile) throws AdfFormatException, ExecutionException {
		List<Instruction> instructions = parseAdf(adfFile);
		read(parseFile, instructions);
	}

	List<Instruction> parseAdf(File adfFile) throws AdfFormatException {
		try {
			FileInputStream fis = new FileInputStream(adfFile);
			return parseAdf(new SpiffParser(fis));
		} catch (FileNotFoundException e) {
			throw new AdfFormatException("File " + adfFile.getAbsolutePath() + " does not exist");
		}
	}

	List<Instruction> parseAdf(InstructionParser parser) throws AdfFormatException {
		try {
            return parser.parse();
		} catch (ParseException e) {
			throw new AdfFormatException("Error in adf file", e);
		}
	}

	void read(File binaryFile, List<Instruction> instructions) throws ExecutionException {
		try {
			FileChannel fc = new FileInputStream(binaryFile).getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int) binaryFile.length());
			fc.read(buffer);
			buffer.flip();

			SpiffVm vm = new SpiffVm(instructions, buffer, eventDispatcher);

			vm.start();

			fc.close();
		} catch (IOException e) {
			throw new ExecutionException("Could not read file " + binaryFile.getAbsolutePath(), e);
		}
	}
}
