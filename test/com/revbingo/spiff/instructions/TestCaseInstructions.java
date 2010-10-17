package com.revbingo.spiff.instructions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.parser.ParseException;

public class TestCaseInstructions {

	ByteBuffer testBuffer;
	byte[] testData;

	Mockery context = new Mockery();
	EventListener ed;

	@Before
	public void setUp() {
		testData = new byte[] { 0x54,0x65,0x73,0x74,0x44,0x61,0x74,0x61,0x21,0x00 };
		testBuffer = ByteBuffer.wrap(testData);
		ed = context.mock(EventListener.class);
		final EventListener dispatcher = ed;
		context.checking(new Expectations() {{
			ignoring(dispatcher).notifyData(with(any(ReferencedInstruction.class)));
			ignoring(dispatcher).notifyGroup(with(any(String.class)), with(any(boolean.class)));
		}});
	}

	@Test
	public void testByteInstruction() throws Exception {
		ByteInstruction unit = new ByteInstruction();

		for(int i = 0; i < 10; i++ ) {
			unit.execute(testBuffer, ed);
			assertThat((Byte) unit.value, is(equalTo(testData[i])));
			assertThat(unit.address, is(equalTo(i)));
		}
	}

	@Test
	public void testShortInstruction() throws Exception {
		ShortInstruction unit = new ShortInstruction();

		short[] expectedValues = new short[] { 0x5465, 0x7374, 0x4461, 0x7461, 0x2100 };

		for(int i = 0; i < 5; i++) {
			unit.execute(testBuffer, ed);
			assertThat((Short) unit.value, is(equalTo(expectedValues[i])));
			assertThat(unit.address, is(equalTo(i*2)));
		}
	}

	@Test
	public void testIntegerInstruction() throws Exception {
		IntegerInstruction unit = new IntegerInstruction();

		int[] expectedValues = new int[] { 0x54657374, 0x44617461 };

		for(int i = 0; i < 2; i++) {
			unit.execute(testBuffer, ed);
			assertThat((Integer) unit.value, is(equalTo(expectedValues[i])));
			assertThat(unit.address, is(equalTo(i*4)));
		}
	}

	@Test
	public void testLongInstruction() throws Exception {
		ByteInstruction padder = new ByteInstruction();
		padder.execute(testBuffer, ed);

		LongInstruction unit = new LongInstruction();

		long expectedValue = 0x6573744461746121L;

		unit.execute(testBuffer, ed);
		assertThat((Long) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(1)));
	}

	@Test
	public void testDoubleInstruction() throws Exception {
		DoubleInstruction unit = new DoubleInstruction();
		unit.address = -1;

		double expectedValue = 3.66552341002185E98d;

		unit.execute(testBuffer, ed);
		assertThat((Double) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testBitsInstruction() throws Exception {
		BitsInstruction unit = new BitsInstruction();
		String numberOfBitsExpression = "13";
		unit.setNumberOfBitsExpr(numberOfBitsExpression);
		unit.execute(testBuffer, ed);

		assertThat(unit.value, instanceOf(boolean[].class));

		boolean[] result = (boolean[]) unit.value;
		assertThat(result.length, is(Integer.valueOf(numberOfBitsExpression)));

		assertThat(testBuffer.position(), is(2));
		assertThat(result[0], is(false));
		assertThat(result[1], is(true));
		assertThat(result[2], is(false));
		assertThat(result[3], is(true));
		assertThat(result[4], is(false));
		assertThat(result[5], is(true));
		assertThat(result[6], is(false));
		assertThat(result[7], is(false));

		assertThat(result[8], is(false));
		assertThat(result[9], is(true));
		assertThat(result[10], is(true));
		assertThat(result[11], is(false));
		assertThat(result[12], is(false));

	}

	@Test
	public void testFloatInstruction() throws Exception {
		FloatInstruction unit = new FloatInstruction();
		unit.address = -1;

		float expectedValue = 3.94193797E12f;

		unit.execute(testBuffer, ed);
		assertThat((Float) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithExplicitEncoding() throws Exception {
		FixedLengthString unit = new FixedLengthString();
		unit.address = -1;

		unit.setEncoding("US-ASCII");
		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("Test")));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithNoExplicitEncodingDropsToASystemDefault() throws Exception {
		FixedLengthString unit = new FixedLengthString();
		unit.address = -1;

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("Test")));
		String s = new String();

		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void fixedLengthStringIsTrimmed() throws Exception {
		FixedLengthString unit = new FixedLengthString();

		byte[] paddedData = new byte[testData.length + 3];
		System.arraycopy(testData, 0, paddedData, 0, testData.length);

		ByteBuffer paddedBuffer = ByteBuffer.wrap(paddedData);
		unit.setLengthExpr("12");
		unit.execute(paddedBuffer, ed);

		assertThat((String) unit.value, is(equalTo("TestData!")));
		String s = new String();
	}

	@Test(expected=ExecutionException.class)
	public void testBadEncodingForStringThrowsException() throws Exception {
		FixedLengthString unit = new FixedLengthString();
		unit.address = -1;

		unit.setEncoding("WTF-8");
		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);
	}

	@Test
	public void testTerminatedStringWithNoExplicitEncodingDropsToASystemDefault() throws Exception {
		TerminatedString unit = new TerminatedString();
		unit.address = -1;

		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("TestData!")));
		String s = new String();

		assertThat(unit.address, is(equalTo(0)));
	}

	@Test(expected=ExecutionException.class)
	public void testBadEncodingForTerminatedStringThrowsException() throws Exception {
		TerminatedString unit = new TerminatedString();
		unit.address = -1;

		unit.setEncoding("WTF-8");
		unit.execute(testBuffer, ed);
	}

	@Test
	public void testLiteralStringInstructionConsumesIfStringMatches() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction();
		unit.setLiteral("TestData!");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is("TestData!"));
	}

	@Test(expected=ExecutionException.class)
	public void testLiteralStringInstructionThrowsExecutionInstructionIfStringDoesNotMatch() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction();
		unit.setLiteral("notData");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is("TestData!"));
	}

	@Test
	public void testTerminatedString() throws Exception {
		TerminatedString unit = new TerminatedString();
		unit.address = -1;
		unit.setEncoding("US-ASCII");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("TestData!")));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test(expected=BufferUnderflowException.class)
	public void testPastEndOfBufferThrowsException() throws ExecutionException {
		LongInstruction unit = new LongInstruction();

		unit.execute(testBuffer, ed);
		unit.execute(testBuffer, ed);
	}

	@Test
	public void testSetOrderInstruction() throws Exception {
		SetOrderInstruction unit = new SetOrderInstruction();

		unit.setOrder(ByteOrder.LITTLE_ENDIAN);
		unit.execute(testBuffer, ed);

		IntegerInstruction test = new IntegerInstruction();
		test.execute(testBuffer, ed);

		assertThat((Integer) test.value, is(equalTo(0x74736554)));

		unit.setOrder(ByteOrder.BIG_ENDIAN);
		unit.execute(testBuffer, ed);

		test = new IntegerInstruction();
		test.execute(testBuffer, ed);

		assertThat((Integer) test.value, is(equalTo(0x44617461)));
	}

	@Test
	public void testUnsignedByteInstruction() throws Exception {
		UnsignedByteInstruction unit = new UnsignedByteInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF });
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Short) unit.value, is(equalTo((short) 0xFF)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithLittleEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Integer) unit.value, is(equalTo(0xFF00)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithBigEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Integer) unit.value, is(equalTo(0x00FF)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithBigEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Long) unit.value, is(equalTo((long) 0xCAFEBABE)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithLittleEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Long) unit.value, is(equalTo((long) 0xBEBAFECA)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testUnsignedLongIsNotSupported() throws Exception {
		new UnsignedLongInstruction().execute(testBuffer, ed);
	}

	@Test
	public void testRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final ReferencedInstruction theInstruction = new ByteInstruction();

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock unit = new RepeatBlock();
		unit.setInstructions(Arrays.asList((Instruction) theInstruction));
		unit.setRepeatCountExpression("10");

		unit.execute(testBuffer, dispatcher);

		context.assertIsSatisfied();
	}

	@Test
	public void testNestedRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final ReferencedInstruction theInstruction = new ByteInstruction();

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock outerUnit = new RepeatBlock();
		outerUnit.setRepeatCountExpression("5");
		RepeatBlock innerUnit = new RepeatBlock();
		innerUnit.setInstructions(Arrays.asList((Instruction) theInstruction));
		innerUnit.setRepeatCountExpression("2");

		outerUnit.setInstructions(Arrays.asList(new Instruction[] { innerUnit }));
		outerUnit.execute(testBuffer, dispatcher);

		context.assertIsSatisfied();
	}

	@Test
	public void testMarkInstruction() throws Exception {
		MarkInstruction unit = new MarkInstruction();
		unit.setName("testMark");

		IntegerInstruction previousInstruction = new IntegerInstruction();
		previousInstruction.execute(testBuffer, ed);
		unit.execute(testBuffer, ed);

		assertThat(Evaluator.evaluateInt("testMark"), is(equalTo(4)));
	}

	@Test
	public void testGroupAndEndGroupInstruction() throws Exception {
		context = new Mockery();
		final EventListener dispatcher = context.mock(EventListener.class);
		final String groupName = "theGroup";

		context.checking(new Expectations() {{
			oneOf(dispatcher).notifyGroup(groupName, true);
			oneOf(dispatcher).notifyGroup(groupName, false);
		}});

		GroupInstruction unitStart = new GroupInstruction(groupName);
		EndGroupInstruction unitEnd = new EndGroupInstruction(groupName);
		unitStart.execute(testBuffer, dispatcher);
		unitEnd.execute(testBuffer, dispatcher);

		context.assertIsSatisfied();
	}

	@Test
	public void blockCanBeUsedAsIterable() throws Exception {
		List<Instruction> insts = Arrays.asList((Instruction) new ByteInstruction("one"), new ByteInstruction("two"));

		Block unit = new Block(insts);

		int count = 0;
		for(Instruction i : unit) {
			count++;
		}
		assertThat(count, is(2));
	}

	@Test(expected=ParseException.class)
	public void numberFactoryThrowsExceptionForUnknownType() throws Exception {
		FixedLengthNumberFactory unit = new FixedLengthNumberFactory();
		unit.getInstruction("oops");
	}

	@Test
	public void referencedInstructionStoresAddressWhenExecuted() throws Exception {
		IntegerInstruction previousInstruction = new IntegerInstruction();
		ReferencedInstruction unit = new ReferencedInstruction() {
			@Override
			public Object evaluate(ByteBuffer buffer) throws ExecutionException {
				return null;
			}
		};

		previousInstruction.execute(testBuffer, ed);
		unit.execute(testBuffer, ed);

		assertThat(unit.getAddress(), is(4));
	}

	@Test
	public void jumpInstruction() throws Exception {
		int positionToJumpTo = 4;
		JumpInstruction unit = new JumpInstruction();
		unit.setExpression(String.valueOf(positionToJumpTo));
		ByteInstruction nextInst = new ByteInstruction();

		unit.execute(testBuffer, ed);
		nextInst.execute(testBuffer, ed);

		assertThat((Byte) nextInst.value, is(testData[positionToJumpTo]));
		assertThat(testBuffer.position(), is(positionToJumpTo + 1));
	}

	@Test
	public void setInstructionAddsExpressionResultToEvaluator() throws Exception {
		Evaluator.clear();

		try {
			Evaluator.evaluateInt("theResult");
			fail("Should not have been able to resolve theResult");
		} catch (ExecutionException e) {}

		SetInstruction unit = new SetInstruction();
		unit.setExpression("8");
		unit.setVarname("theResult");

		unit.execute(testBuffer, ed);

		assertThat(Evaluator.evaluateInt("theResult"), is(8));
	}

	@Test
	public void printInstructionPrintsToStdOut() throws Exception {
		PrintStream oldOut = System.out;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream newOut = new PrintStream(baos);
		System.setOut(newOut);

		assertThat(baos.size(), is(0));

		PrintInstruction unit = new PrintInstruction();
		unit.setVar("1 + 3");

		unit.execute(testBuffer, ed);

		assertThat(baos.size(), is(not(0)));
		System.setOut(oldOut);
	}

	@Test
	public void ifBlockRunsIfInstructionsWhenExpressionIsTrue() throws Exception {
		ByteInstruction ifInst = new ByteInstruction();
		List<Instruction> ifInsts = Arrays.asList((Instruction) ifInst);

		JumpInstruction elseInst1 = new JumpInstruction();
		elseInst1.setExpression("3");
		ByteInstruction elseInst2 = new ByteInstruction();
		List<Instruction> elseInsts = Arrays.asList(elseInst1, elseInst2);

		IfBlock unit = new IfBlock();
		unit.setIfExpression("1 == 1");
		unit.setInstructions(ifInsts);
		unit.setElseInstructions(elseInsts);

		unit.execute(testBuffer, ed);

		assertThat((Byte) ifInst.value, is((byte) 0x54));
		assertThat(elseInst2.value, is(nullValue()));
	}

	@Test
	public void ifBlockRunsElseInstructionsWhenExpressionIsFalse() throws Exception {
		ByteInstruction ifInst = new ByteInstruction();
		List<Instruction> ifInsts = Arrays.asList((Instruction) ifInst);

		JumpInstruction elseInst1 = new JumpInstruction();
		elseInst1.setExpression("3");
		ByteInstruction elseInst2 = new ByteInstruction();
		List<Instruction> elseInsts = Arrays.asList(elseInst1, elseInst2);

		IfBlock unit = new IfBlock();
		unit.setIfExpression("1 != 1");
		unit.setInstructions(ifInsts);
		unit.setElseInstructions(elseInsts);

		unit.execute(testBuffer, ed);

		assertThat((Byte) elseInst2.value, is((byte) 0x74));
		assertThat(ifInst.value, is(nullValue()));
	}
}
