/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.parser;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;
import java.nio.ByteOrder;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.instructions.BitsInstruction;
import com.revbingo.spiff.instructions.ByteInstruction;
import com.revbingo.spiff.instructions.BytesInstruction;
import com.revbingo.spiff.instructions.DoubleInstruction;
import com.revbingo.spiff.instructions.EndGroupInstruction;
import com.revbingo.spiff.instructions.FixedLengthString;
import com.revbingo.spiff.instructions.FloatInstruction;
import com.revbingo.spiff.instructions.GroupInstruction;
import com.revbingo.spiff.instructions.IfBlock;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.JumpInstruction;
import com.revbingo.spiff.instructions.LiteralStringInstruction;
import com.revbingo.spiff.instructions.LongInstruction;
import com.revbingo.spiff.instructions.MarkInstruction;
import com.revbingo.spiff.instructions.PrintInstruction;
import com.revbingo.spiff.instructions.RepeatBlock;
import com.revbingo.spiff.instructions.SetInstruction;
import com.revbingo.spiff.instructions.SetOrderInstruction;
import com.revbingo.spiff.instructions.ShortInstruction;
import com.revbingo.spiff.instructions.SkipInstruction;
import com.revbingo.spiff.instructions.TerminatedString;
import com.revbingo.spiff.instructions.UnsignedByteInstruction;
import com.revbingo.spiff.instructions.UnsignedIntegerInstruction;
import com.revbingo.spiff.instructions.UnsignedLongInstruction;
import com.revbingo.spiff.instructions.UnsignedShortInstruction;

public class TestCaseSpiffParser {

	@Test
	public void blankLinesAtEndOfFileDoNotCauseAnError() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte testByte")
			.add("")
			.add("")
			.end();

		parse(adf);
	}

	@Test
	public void primtiveDataTypesGenerateCorrectInstructions() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte testByte")
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
	public void bitsTakesArgForNumberOfBits() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("bits(20) theBits")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(BitsInstruction.class));
	}

	@Test
	public void bytesTakesArgForNumberOfBytes() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("bytes(length + 1) theBytes")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(BytesInstruction.class));
		assertThat(((BytesInstruction) insts.get(0)).getLengthExpr(), is("length+1"));
	}

	@Test
	public void parenthesesWithExpressionAfterKeywordIndicatesFixedLengthString() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string(expr) str")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(FixedLengthString.class));

		FixedLengthString inst = (FixedLengthString) insts.get(0);
		assertThat(inst.getLengthExpr(), is("expr"));
	}

	@Test
	public void fixedLengthStringCanAlsoSpecifyEncoding() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string(expr, UTF-8) str")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(FixedLengthString.class));

		FixedLengthString inst = (FixedLengthString) insts.get(0);
		assertThat(inst.getLengthExpr(), is("expr"));
		assertThat(inst.getEncoding().displayName(), is("UTF-8"));
	}

	@Test(expected=ParseException.class)
	public void emptyParenthesesAfterKeywordThrowsException() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string() str")
			.end();

		parse(adf);
	}

	@Test
	public void lackOfParenthesesAfterStringKeywordIndicatesNullTerminatedString() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string terminatedString")
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(TerminatedString.class));
	}

	@Test
	public void parenthesesWithJustEncodingAfterStringKeywordIndicatesNullTerminatedString() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string(UTF-8) terminatedString")
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(TerminatedString.class));

		TerminatedString str = (TerminatedString) insts.get(0);
		assertThat(str.getEncoding().displayName(), is("UTF-8"));
	}

	@Test
	public void stringWithLiteralGeneratesLiteralStringInstruction() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string('mhbd') m")
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(LiteralStringInstruction.class));

		LiteralStringInstruction litStr = (LiteralStringInstruction) insts.get(0);
		assertThat(litStr.getLiteral(), is("mhbd"));
	}

	@Test
	public void stringWithLiteralCanTakeOptionalEncoding() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("string('mhbd', UTF-8) m")
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(LiteralStringInstruction.class));

		LiteralStringInstruction litStr = (LiteralStringInstruction) insts.get(0);
		assertThat(litStr.getLiteral(), is("mhbd"));
		assertThat(litStr.getEncoding().displayName(), is("UTF-8"));
	}

	@Test
	public void lineEndingsAreRecognised() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte byteOne\n", false)
			.add("byte byteTwo\r", false)
			.add("byte byteThree\r\n", false)
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(3));
	}

	@Test
	public void setOrder() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".setorder LITTLE_ENDIAN")
			.add(".setorder BIG_ENDIAN")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));

		assertThat(insts.get(0), instanceOf(SetOrderInstruction.class));
		assertThat(((SetOrderInstruction) insts.get(0)).getOrder(), is(ByteOrder.LITTLE_ENDIAN));

		assertThat(insts.get(1), instanceOf(SetOrderInstruction.class));
		assertThat(((SetOrderInstruction) insts.get(1)).getOrder(), is(ByteOrder.BIG_ENDIAN));
	}

	@Test
	public void encodingsAppliedToSubsequentStringInstructions() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".setencoding UTF-16LE")
			.add("string utf16le")
			.add(".setencoding UTF-16BE")
			.add("string utf16be")
			.add(".setencoding UTF-8")
			.add("string utf8")
			.add(".setencoding US-ASCII")
			.add("string usacii")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(4));

		assertThat(insts.get(0), instanceOf(TerminatedString.class));
		assertThat(((TerminatedString) insts.get(0)).getEncoding().displayName(), is("UTF-16LE"));

		assertThat(insts.get(1), instanceOf(TerminatedString.class));
		assertThat(((TerminatedString) insts.get(1)).getEncoding().displayName(), is("UTF-16BE"));

		assertThat(insts.get(2), instanceOf(TerminatedString.class));
		assertThat(((TerminatedString) insts.get(2)).getEncoding().displayName(), is("UTF-8"));

		assertThat(insts.get(3), instanceOf(TerminatedString.class));
		assertThat(((TerminatedString) insts.get(3)).getEncoding().displayName(), is("US-ASCII"));
	}

	@Test
	public void specifiedEncodingOverridesCurrentDefault() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".setencoding US-ASCII")
			.add("string(UTF-8) utf8")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((TerminatedString) insts.get(0)).getEncoding().displayName(), is("UTF-8"));
	}

	@Test
	public void jumpInstructionTakesLiteralOrExpressionForAddressToJumpTo() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".jump 14")
			.add(".jump x - 1")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));
		assertThat(insts.get(0), instanceOf(JumpInstruction.class));
		assertThat(insts.get(1), instanceOf(JumpInstruction.class));
	}

	@Test(expected=ParseException.class)
	public void jumpInvalidIfNoAddressSpecified() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".jump")
			.end();

		parse(adf);
	}

	@Test
	public void skipInstructionTakesLiteralOrExpressionForLengthToSkip() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".skip 14")
			.add(".skip x - 1")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));
		assertThat(insts.get(0), instanceOf(SkipInstruction.class));
		assertThat(insts.get(1), instanceOf(SkipInstruction.class));
	}

	@Test(expected=ParseException.class)
	public void skipInvalidIfNoExpressionSpecified() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".skip")
			.end();

		parse(adf);
	}

	@Test
	public void setInstructionTakesVarNameAndExpressionAsParams() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".set abc 123")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(SetInstruction.class));
	}

	@Test
	public void setInstructionFailsIfNameAndExpressionNotProvided() throws Exception {
		try {
			AdfFile adf = AdfFile.start()
				.add(".set")
				.end();
			parse(adf);
			fail(".set with no args should fail");
		} catch (ParseException e) {}

		try {
			AdfFile adf = AdfFile.start()
				.add(".set a")
				.end();
			parse(adf);
			fail(".set with one arg should fail");
		} catch (ParseException e) {}

		try {
			AdfFile adf = AdfFile.start()
				.add(".set a b c")
				.end();
			parse(adf);
			fail(".set with more than two args should fail");
		} catch (ParseException e) {}
	}

	@Test
	public void printInstructionTakesOneArg() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".print x")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(PrintInstruction.class));
	}

	@Test
	public void markInstructionTakesMarkNameAsArg() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".mark theMark")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(MarkInstruction.class));
	}

	@Test
	public void defineAndInclude() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".define(aBlock) {")
			.add("  byte byteOne")
		    .add("}")

		    .add(".define(anotherBlock) {")
		    .add("  byte byteTwo")
		    .add("}")

		    .add(".include anotherBlock")
		    .add(".include aBlock")
		    .add(".include aBlock")
		    .end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(3));
		assertThat(insts.get(0), instanceOf(ByteInstruction.class));
		assertThat(insts.get(1), instanceOf(ByteInstruction.class));
		assertThat(insts.get(2), instanceOf(ByteInstruction.class));

		assertThat(((ByteInstruction) insts.get(0)).getName(), is("byteTwo"));
		assertThat(((ByteInstruction) insts.get(1)).getName(), is("byteOne"));
		assertThat(((ByteInstruction) insts.get(2)).getName(), is("byteOne"));
	}

	@Test
	public void blockInstructionsCanHaveBracesWithOrWithoutSpacesSurrounding() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".group(nospace){")
			.add("  byte byteOne")
			.add("}")

			.add(".group(spacesBefore)  {")
			.add("  byte byteTwo")
			.add("  }")

			.add(".group(spacesAfter){   ")
			.add("  byte byteThree")
			.add("}   ")

			.add(".group(spacesBeforeAndAfter)  {  ")
			.add("  byte byteFour")
			.add("  }  ")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(12));
	}

	@Test
	public void repeatTakesExpressionAndIncludesInstructionsFromTheBlock() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".repeat(x) {")
			.add("   byte byteOne")
			.add("   byte byteTwo")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(RepeatBlock.class));

		RepeatBlock theBlock = (RepeatBlock) insts.get(0);

		assertThat(theBlock.getInstructions().size(), is(2));
		assertThat(theBlock.getRepeatCountExpression(), is("x"));
	}

	@Test
	public void repeatRequiresExpression() throws Exception {
		try {
			AdfFile adf = AdfFile.start()
				.add(".repeat {")
				.add("}")
				.end();
			parse(adf);
			fail("Should not be able to have repeat without expression");
		} catch(ParseException e) {}

		try {
			AdfFile adf = AdfFile.start()
				.add(".repeat() {")
				.add("}")
				.end();
			parse(adf);
			fail("Should not be able to have repeat without expression");
		} catch(ParseException e) {}

	}

	@Test
	public void groupGeneratesGroundAndEndGroupInstructions() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".group(groupName) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));
		assertThat(insts.get(0), instanceOf(GroupInstruction.class));
		assertThat(insts.get(1), instanceOf(EndGroupInstruction.class));

		assertThat(((GroupInstruction) insts.get(0)).getGroupName(), is("groupName"));
		assertThat(((EndGroupInstruction) insts.get(1)).getGroupName(), is("groupName"));
	}

	@Test
	public void ifElse() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(x) {")
			.add("   byte byteOne")
			.add("} .else {")
			.add("  byte byteTwo")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(IfBlock.class));

		IfBlock theBlock = (IfBlock) insts.get(0);

		assertThat(theBlock.getIfInstructions().getInstructions().size(), is(1));
		assertThat(theBlock.getElseInstructions().getInstructions().size(), is(1));
	}

	@Test
	public void elseCanBeOnSeparateLine() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(x) {")
			.add("   byte byteOne")
			.add("}")
			.add(".else {")
			.add("  byte byteTwo")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(IfBlock.class));

		adf = AdfFile.start()
			.add(".if(x) {")
			.add("   byte byteOne")
			.add("}")
			.add(".else")
			.add("{")
			.add("  byte byteTwo")
			.add("}")
			.end();

		insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(IfBlock.class));
	}

	@Test(expected=ParseException.class)
	public void elseWithoutIfIsIllegal() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".else {")
			.add("}")
			.end();

		parse(adf);
	}

	@Test
	public void commentsCanBeOnSameLineAsAnInstruction() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte byteOne  #comment")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
	}

	@Test
	public void commentCanBeOnItsOwnLine() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("#comment")
			.end();

		parse(adf);
	}

	@Test
	public void expressionOperators() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(x+y-z/3*(j*k) != 5) {")
			.add("  .if(a == b%3) {")
			.add("    .if(a >= 3) {")
			.add("      .if(b < 4) {")
			.add("         .if(b <= 10) {")
			.add("            .if(c > 6) {")
			.add("}")
			.add("}")
			.add("}")
			.add("}")
			.add("}")
			.add("}")
			.end();

		parse(adf);
	}

	@Test
	public void expressionCanContainFunctions() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(pow(2,3) == 2) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("pow(2,3)==2"));
	}

	@Test
	public void expressionCanContainFunctionWithSingleArg() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".repeat(ceil(biWidth/8)) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((RepeatBlock) insts.get(0)).getRepeatCountExpression(), is("ceil(biWidth/8)"));
	}

	@Test
	public void expressionsCanContainNegativeNumbers() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(abs(-2) == 2) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("abs(-2)==2"));
	}

	@Test
	public void canReferenceValueExplicitly() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(byteOne.value == 2) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("byteOne.value==2"));
	}

	@Test
	public void canReferenceAddressUsingAmpersandNotation() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".if(&byteOne == 3) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("byteOne.address==3"));
	}

	private List<Instruction> parse(AdfFile adf) throws Exception {
		SpiffParser unit = new SpiffParser(adf.asReader());
		unit.start();
		return unit.getInstructions();
	}

	private static class AdfFile {

		private StringBuffer buffer = new StringBuffer();

		public static AdfFile start() {
			return new AdfFile();
		}

		public AdfFile add(String line) {
			return add(line, true);
		}

		public AdfFile add(String line, boolean cr) {
			buffer.append(line);
			if(cr) buffer.append("\n");
			return this;
		}

		public AdfFile end() {
			return this;
		}

		public Reader asReader() {
			return new StringReader(buffer.toString());
		}
	}
}
