/*
 * Copyright Mark Piper 2016
 *
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
 */
package com.revbingo.spiff;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.evaluator.EvalExEvaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.InstructionParser;

@RunWith(JMock.class)
public class TestCaseBinaryParser {

	Mockery context = new Mockery();

	final List<Instruction> NO_INSTRUCTIONS = Collections.emptyList();

	@Test(expected = AdfFormatException.class)
	public void parseAdfThrowsExceptionForNonExistantFile() throws Exception {
		BinaryParser unit = new BinaryParser();

		unit.parseAdf(new File("not/a/file"));
	}

	@Test(expected = AdfFormatException.class)
	public void exceptionWhenAdfFileIsUnparseable() throws Exception {
		File badAdf = File.createTempFile("temp", "txt");

		PrintWriter pw = new PrintWriter(badAdf);
		pw.write("aloadofbollocks");
		pw.close();

		BinaryParser unit = new BinaryParser();

		unit.parseAdf(badAdf);
	}

	@Test(expected = ExecutionException.class)
	public void exceptionWhenFileIsMissing() throws Exception {
		BinaryParser unit = new BinaryParser();

		unit.read(new File("not/a/file"), NO_INSTRUCTIONS);
	}

	@Test(expected = ExecutionException.class)
	public void exceptionWhenFileCannotBeRead() throws Exception {
		File unreadableFile = File.createTempFile("unreadable", "txt");
		boolean wasMadeUnreadable = unreadableFile.setReadable(false);
		assertThat(wasMadeUnreadable, is(true));

		BinaryParser unit = new BinaryParser();
		unit.read(unreadableFile, NO_INSTRUCTIONS);
	}

	@Test
	public void parseAdfStartsParserAndGetsListOfInstructions() throws Exception {
		context = new Mockery();

		final ByteInstruction one = new ByteInstruction("one");
		final ByteInstruction two = new ByteInstruction("two");
		final ByteInstruction three = new ByteInstruction("three");

		final InstructionParser mockParser = context.mock(InstructionParser.class);

		context.checking(new Expectations(){{
			oneOf(mockParser).parse();
				will(returnValue(Arrays.asList(one, two, three)));
		}});

		BinaryParser unit = new BinaryParser();
		List<Instruction> instructions = unit.parseAdf(mockParser);

		assertThat(instructions.size(), is(3));
	}

	@Test
	public void readExecutesEachInstruction() throws Exception {
		context = new Mockery();

		final Instruction mockInstruction = context.mock(Instruction.class);
		final EventListener eventDispatcher = context.mock(EventListener.class);

		context.checking(new Expectations() {{
			exactly(3).of(mockInstruction).execute(with(aNonNull(ByteBuffer.class)), with(same(eventDispatcher)), with(any(EvalExEvaluator.class)));
		}});

		List<Instruction> instructions = Arrays.asList(mockInstruction, mockInstruction, mockInstruction);

		BinaryParser parser = new BinaryParser(eventDispatcher);
		parser.read(File.createTempFile("samplebytes", "tmp"), instructions);
	}

}
