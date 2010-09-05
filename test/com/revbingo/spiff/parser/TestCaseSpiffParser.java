package com.revbingo.spiff.parser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.DoubleInstruction;
import com.revbingo.spiff.instructions.FixedLengthString;
import com.revbingo.spiff.instructions.FloatInstruction;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.LongInstruction;
import com.revbingo.spiff.instructions.ShortInstruction;
import com.revbingo.spiff.instructions.TerminatedString;
import com.revbingo.spiff.instructions.UnsignedByteInstruction;
import com.revbingo.spiff.instructions.UnsignedIntegerInstruction;
import com.revbingo.spiff.instructions.UnsignedLongInstruction;
import com.revbingo.spiff.instructions.UnsignedShortInstruction;

public class TestCaseSpiffParser {

	@Test(expected=ParseException.class)
	public void emptyFileThrowsException() throws Exception {
		AdfFile adf = new AdfFile();
		parse(adf);
	}
	
	@Test
	public void primtiveDataTypesGenerateCorrectInstructions() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("byte testByte")
			.add("int testInt")
			.add("long testLong")
			.add("short testShort")
			.add("double testDouble")
			.add("float testFloat")
			
			.add("ubyte testUByte")
			.add("ushort testUShort")
			.add("uint testUInt")
			.add("ulong testULong")
			.end();
		
		List<Instruction> insts = parse(adf);
		
		assertThat(insts.size(), is(10));
		assertThat(insts.get(0), instanceOf(ByteInstruction.class));
		assertThat(insts.get(1), instanceOf(IntegerInstruction.class));
		assertThat(insts.get(2), instanceOf(LongInstruction.class));
		assertThat(insts.get(3), instanceOf(ShortInstruction.class));
		assertThat(insts.get(4), instanceOf(DoubleInstruction.class));
		assertThat(insts.get(5), instanceOf(FloatInstruction.class));
		assertThat(insts.get(6), instanceOf(UnsignedByteInstruction.class));
		assertThat(insts.get(7), instanceOf(UnsignedShortInstruction.class));
		assertThat(insts.get(8), instanceOf(UnsignedIntegerInstruction.class));
		assertThat(insts.get(9), instanceOf(UnsignedLongInstruction.class));
	}
	
	@Test
	public void parenthesesWithExpressionAfterKeywordIndicatesFixedLengthString() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("string(expr) str").end();
		
		List<Instruction> insts = parse(adf);
		
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(FixedLengthString.class));
		
		FixedLengthString inst = (FixedLengthString) insts.get(0); 
		assertThat(inst.getLengthExpr(), is("expr"));
	}
	
	@Test
	public void fixedLengthStringCanAlsoSpecifyEncoding() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("string(expr, UTF-8) str").end();
		
		List<Instruction> insts = parse(adf);
		
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(FixedLengthString.class));
		
		FixedLengthString inst = (FixedLengthString) insts.get(0); 
		assertThat(inst.getLengthExpr(), is("expr"));
		assertThat(inst.getEncoding(), is("UTF-8"));
	}

	@Test(expected=ParseException.class)
	public void emptyParenthesesAfterKeywordThrowsException() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("string() str").end();
		
		parse(adf);
	}
	
	@Test
	public void lackOfParenthesesAfterStringKeywordIndicatesNullTerminatedString() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("string terminatedString").end();
		
		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(TerminatedString.class));
	}
	
	@Test
	public void parenthesesWithJustEncodingAfterStringKeywordIndicatesNullTerminatedString() throws Exception {
		AdfFile adf = new AdfFile();
		
		adf.add("string(UTF-8) terminatedString").end();
		
		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(TerminatedString.class));
		
		TerminatedString str = (TerminatedString) insts.get(0);
		assertThat(str.getEncoding(), is("UTF-8"));
	}
	private List<Instruction> parse(AdfFile adf) throws Exception {
		SpiffParser unit = new SpiffParser(adf.asReader());
		unit.start();
		return unit.getInstructions();
	}
	
	private class AdfFile {
		
		private StringBuffer buffer = new StringBuffer();
		
		public AdfFile add(String line) {
			buffer.append(line);
			buffer.append("\n");
			return this;
		}
		
		public AdfFile end() {
			buffer.append("END\n");
			return this;
		}
		
		public Reader asReader() {
			return new StringReader(buffer.toString());
		}
	}
}
