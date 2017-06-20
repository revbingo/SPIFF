package com.revbingo.spiff.parser;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.*;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.instructions.*;
import com.revbingo.spiff.parser.gen.ParseException;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(9));
		assertThat(insts.get(0), instanceOf(ByteInstruction.class));
		assertThat(((Datatype) insts.get(0)).getName(), is("testByte"));
		assertThat(insts.get(1), instanceOf(IntegerInstruction.class));
		assertThat(((Datatype) insts.get(1)).getName(), is("testInt"));
		assertThat(insts.get(2), instanceOf(LongInstruction.class));
		assertThat(((Datatype) insts.get(2)).getName(), is("testLong"));
		assertThat(insts.get(3), instanceOf(ShortInstruction.class));
		assertThat(((Datatype) insts.get(3)).getName(), is("testShort"));
		assertThat(insts.get(4), instanceOf(DoubleInstruction.class));
		assertThat(((Datatype) insts.get(4)).getName(), is("testDouble"));
		assertThat(insts.get(5), instanceOf(FloatInstruction.class));
		assertThat(((Datatype) insts.get(5)).getName(), is("testFloat"));
		assertThat(insts.get(6), instanceOf(UnsignedByteInstruction.class));
		assertThat(((Datatype) insts.get(6)).getName(), is("testUByte"));
		assertThat(insts.get(7), instanceOf(UnsignedShortInstruction.class));
		assertThat(((Datatype) insts.get(7)).getName(), is("testUShort"));
		assertThat(insts.get(8), instanceOf(UnsignedIntegerInstruction.class));
		assertThat(((Datatype) insts.get(8)).getName(), is("testUInt"));
	}

	@Test
	public void numericLiteralsAreUnderstood() throws Exception {
		AdfFile adf = AdfFile.start()
				.add("byte(0x80) testByte")
				.add("int(0xFFFFFFFF) testInt")
				.add("long(0xFFFFFFFFFF) testLong")
				.add("short(10) testShort")
				.add("double(1.234) testDouble")
				.add("float(1.234) testFloat")

				.add("ubyte(0xab) testUByte")
				.add("ushort(0xFFCC) testUShort")
				.add("uint(0xABCD) testUInt")
				.end();
	
		List<Instruction> insts = parse(adf);

		assertThat(insts.get(0), instanceOf(ByteInstruction.class));
		assertThat(((ByteInstruction) insts.get(0)).getLiteralExpr(), is("0x80"));
		assertThat(((Datatype) insts.get(0)).getName(), is("testByte"));
		assertThat(insts.get(1), instanceOf(IntegerInstruction.class));
		assertThat(((Datatype) insts.get(1)).getName(), is("testInt"));
		assertThat(((IntegerInstruction) insts.get(1)).getLiteralExpr(), is("0xFFFFFFFF"));
		
		assertThat(insts.get(2), instanceOf(LongInstruction.class));
		assertThat(((Datatype) insts.get(2)).getName(), is("testLong"));
		assertThat(((LongInstruction) insts.get(2)).getLiteralExpr(), is("0xFFFFFFFFFF"));
		
		assertThat(insts.get(3), instanceOf(ShortInstruction.class));
		assertThat(((Datatype) insts.get(3)).getName(), is("testShort"));
		assertThat(((ShortInstruction) insts.get(3)).getLiteralExpr(), is("10"));
		
		assertThat(insts.get(4), instanceOf(DoubleInstruction.class));
		assertThat(((Datatype) insts.get(4)).getName(), is("testDouble"));
		assertThat(((DoubleInstruction) insts.get(4)).getLiteralExpr(), is("1.234"));
		
		assertThat(insts.get(5), instanceOf(FloatInstruction.class));
		assertThat(((Datatype) insts.get(5)).getName(), is("testFloat"));
		assertThat(((FloatInstruction) insts.get(5)).getLiteralExpr(), is("1.234"));
		
		assertThat(insts.get(6), instanceOf(UnsignedByteInstruction.class));
		assertThat(((Datatype) insts.get(6)).getName(), is("testUByte"));
		assertThat(((UnsignedByteInstruction) insts.get(6)).getLiteralExpr(), is("0xab"));
		
		assertThat(insts.get(7), instanceOf(UnsignedShortInstruction.class));
		assertThat(((Datatype) insts.get(7)).getName(), is("testUShort"));
		assertThat(((UnsignedShortInstruction) insts.get(7)).getLiteralExpr(), is("0xFFCC"));
		
		assertThat(insts.get(8), instanceOf(UnsignedIntegerInstruction.class));
		assertThat(((Datatype) insts.get(8)).getName(), is("testUInt"));
		assertThat(((UnsignedIntegerInstruction) insts.get(8)).getLiteralExpr(), is("0xABCD"));
	}
	
	@Test
	public void instructionsHaveLineNumbers() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte theByte")
			.add("")
			.add("int theInt")
			.add("#comment")
			.add(".repeat(x) {")
			.add("   byte anotherByte")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(3));
		assertThat(((AdfInstruction) insts.get(0)).getLineNumber(), is(1));
		assertThat(((AdfInstruction) insts.get(1)).getLineNumber(), is(3));
		assertThat(((AdfInstruction) insts.get(2)).getLineNumber(), is(5));
		assertThat(((AdfInstruction) ((RepeatBlock) insts.get(2)).getInstructions().get(0)).getLineNumber(), is(6));
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
		assertThat(inst.getName(), is("str"));
		assertThat(inst.getExpression(), is("expr"));
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
		assertThat(inst.getExpression(), is("expr"));
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
		assertThat(((TerminatedString) insts.get(0)).getName(), is("terminatedString"));
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
			.add("string('mhbd') dbhm")
			.end();

		List<Instruction> insts = parse(adf);
		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(LiteralStringInstruction.class));

		LiteralStringInstruction litStr = (LiteralStringInstruction) insts.get(0);
		assertThat(litStr.getExpression(), is("mhbd"));
		assertThat(litStr.getName(), is("dbhm"));
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
		assertThat(litStr.getExpression(), is("mhbd"));
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
		assertThat(((SkipInstruction) insts.get(0)).getExpression(), is("14"));
		assertThat(insts.get(1), instanceOf(SkipInstruction.class));
		assertThat(((SkipInstruction) insts.get(1)).getExpression(), is("x-1"));
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

	@Test(expected=AdfFormatException.class)
	public void includingUnknownBlockNameThrowsException() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".include notDefined")
			.end();

		parse(adf);
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

		assertThat(((GroupInstruction) insts.get(0)).getExpression(), is("groupName"));
		assertThat(((EndGroupInstruction) insts.get(1)).getExpression(), is("groupName"));
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

		assertThat(theBlock.getInstructions().size(), is(1));
		assertThat(theBlock.getElseBlock().getInstructions().size(), is(1));
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
			.add("              .if((c & 0xFF) == a | 0xFF) {")
			.add("}")
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
	public void expressionWithMinus() throws Exception {
		AdfFile adf = AdfFile.start()
		.add(".jump x/-2")
		.add(".jump x-2")
		.add(".jump -2 + 3 / (-4)")
		.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(3));
		assertThat(((JumpInstruction) insts.get(0)).getExpression(), is("x/-2"));
		assertThat(((JumpInstruction) insts.get(1)).getExpression(), is("x-2"));
		assertThat(((JumpInstruction) insts.get(2)).getExpression(), is("-2+3/(-4)"));
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
			.add(".if(abs(-2)/-3 == 2) {")
			.add("}")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("abs(-2)/-3==2"));
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
		assertThat(((IfBlock) insts.get(0)).getIfExpression(), is("byteOne_address==3"));
	}

	@Test
	public void canUseHexNotationInExpressions() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".skip 0xFF")
			.add(".skip 0xCA01FF0")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));
	}

	@Test
	public void hexNotationIsCaseInsensitive() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".skip 0xFf")
			.add(".skip 0xcafebabe")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(2));
	}

	@Test
	public void canReferenceUserDefinedDataType() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".datatype mystring com.revbingo.spiff.instructions.StringReversingInstruction")
			.add("mystring  customInt")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(insts.size(), is(1));
		assertThat(insts.get(0), instanceOf(StringReversingInstruction.class));
		assertThat(((Datatype) insts.get(0)).getName(), is("customInt"));
	}

	@Test(expected=AdfFormatException.class)
	public void unknownClassTypeThrowsException() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".datatype badClass bad.classname")
			.end();

		parse(adf);
	}

	@Test(expected=AdfFormatException.class)
	public void undefinedDatatypeThrowsException() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("undefinedType  notDefined")
			.end();

		parse(adf);
	}

	@Test(expected=AdfFormatException.class)
	public void classNotExtendingDatatypeCannotBeUsedForCustomDatatype() throws Exception {
		AdfFile adf = AdfFile.start()
		.add(".datatype badClass java.lang.String")
		.end();

		parse(adf);
	}

	@Test(expected=AdfFormatException.class)
	public void classWithoutNoArgsConstructorCannotBeUsedForCustomDatatype() throws Exception {
		AdfFile adf = AdfFile.start()
		.add(".datatype badClass com.revbingo.spiff.datatypes.FixedLengthString")
		.add("badClass  wotNoArgs")
		.end();

		parse(adf);
	}

	@Test(expected=AdfFormatException.class)
	public void classWithoutPublicConstructorCannotBeUsedForCustomDatatype() throws Exception {
		AdfFile adf = AdfFile.start()
		.add(".datatype badClass com.revbingo.spiff.parser.TestCaseSpiffParser$PrivateConstructor")
		.add("badClass  wotNoArgs")
		.end();

		parse(adf);
	}

	@Test
	public void canUseUnderscoreInIdentifier() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte the_byte")
			.add("int _beginsWithUnderscore")
			.end();

		List<Instruction> insts = parse(adf);

		assertThat(((Datatype) insts.get(0)).getName(), is("the_byte"));
		assertThat(((Datatype) insts.get(1)).getName(), is("_beginsWithUnderscore"));
	}

	private List<Instruction> parse(AdfFile adf) throws Exception {
		SpiffParser unit = new SpiffParser(adf.asInputStream());
		return unit.parse();
	}

	@SuppressWarnings("unused")
	public static class PrivateConstructor extends Datatype {

		private PrivateConstructor() { super("aName"); }

		@Override
		public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
			return new Object();
		}
	}
}
