package com.revbingo.spiff;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.internal.ExpectationBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.ReferencedInstruction;
import com.sun.jmx.snmp.Timestamp;

@RunWith(JMock.class)
public class TestCaseBinaryParser {

	Mockery context = new Mockery();
	
	@Test(expected = AdfFormatException.class)
	public void parseAdfThrowsExceptionForNonExistantFile() throws Exception {
		BinaryParser unit = new BinaryParser(null);

		unit.parseAdf(new File("not/a/file"));
	}

	@Test(expected = AdfFormatException.class)
	public void exceptionWhenAdfFileIsUnparseable() throws Exception {
		File badAdf = File.createTempFile("temp", "txt");

		PrintWriter pw = new PrintWriter(badAdf);
		pw.write("aloadofbollocks");
		pw.close();

		BinaryParser unit = new BinaryParser(null);

		unit.parseAdf(badAdf);
	}

	@Test(expected = ExecutionException.class)
	public void exceptionWhenFileIsMissing() throws Exception {
		BinaryParser unit = new BinaryParser(null);

		unit.read(new File("not/a/file"), null);
	}

	@Test(expected = ExecutionException.class)
	public void exceptionWhenFileCannotBeRead() throws Exception {
		File unreadableFile = File.createTempFile("unreadable", "txt");
		unreadableFile.setReadable(false);

		BinaryParser unit = new BinaryParser(null);
		unit.read(unreadableFile, null);
	}

	@Test
	public void parseAdfReturnsListOfInstructions() throws Exception {
		StringWriter str = new StringWriter();
		PrintWriter pw = new PrintWriter(str);
		pw.println("int one");
		pw.println("short two");
		pw.println("long three");
		pw.println("END");

		BinaryParser unit = new BinaryParser(null);
		List<Instruction> instructions = unit
				.parseAdf(new ByteArrayInputStream(str.getBuffer().toString().getBytes()));

		assertThat(instructions.size(), is(3));
	}

	@Test
	public void readExecutesEachInstruction() throws Exception {
		context = new Mockery();
		
		final Instruction mockInstruction = context.mock(Instruction.class);
		final EventDispatcher eventDispatcher = context.mock(EventDispatcher.class);
		
		context.checking(new Expectations() {{
			exactly(3).of(mockInstruction).execute(with(aNonNull(ByteBuffer.class)), with(same(eventDispatcher)));
		}});
		
		List<Instruction> instructions = Arrays.asList(new Instruction[] { mockInstruction, mockInstruction, mockInstruction });
		
		BinaryParser parser = new BinaryParser(eventDispatcher);
		parser.read(File.createTempFile("samplebytes", "tmp"), instructions);
		
	}
	
}
