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
package com.revbingo.spiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.InstructionParser;
import com.revbingo.spiff.parser.ParseException;
import com.revbingo.spiff.parser.SpiffParser;

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
            parser.start();
            return parser.getInstructions();
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

			Evaluator.addVariable("fileLength", (int) binaryFile.length());

			for(Instruction ins : instructions){
				ins.execute(buffer, eventDispatcher);
			}

			fc.close();
		} catch (IOException e) {
			throw new ExecutionException("Could not read file " + binaryFile.getAbsolutePath(), e);
		}
	}
}
