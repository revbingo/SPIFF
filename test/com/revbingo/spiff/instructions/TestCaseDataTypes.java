/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.parser.ParseException;

public class TestCaseDataTypes {
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
	public void testBytesInstruction() throws Exception {
		BytesInstruction unit = new BytesInstruction();
		unit.setLengthExpr("5");
		unit.execute(testBuffer, ed);

		assertThat(unit.value, instanceOf(byte[].class));
		byte[] bytes = (byte[]) unit.value;
		assertThat(bytes.length, is(5));
		assertThat(bytes, is(new byte[] { 0x54,0x65,0x73,0x74,0x44}));
		assertThat(testBuffer.position(), is(5));
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
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.address = -1;

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("Test")));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithNoExplicitEncodingDropsToASystemDefault() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.address = -1;

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is(equalTo("Test")));
		String s = new String();

		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void fixedLengthStringIsTrimmed() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");

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
		new FixedLengthString("HMMM");
	}

	@Test(expected=ExecutionException.class)
	public void testBadEncodingForTerminatedStringThrowsException() throws Exception {
		new TerminatedString("HMMM");
	}

	@Test
	public void testLiteralStringInstructionConsumesIfStringMatches() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction("US-ASCII");
		unit.setLiteral("TestData!");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is("TestData!"));
	}

	@Test
	public void literalStringWorksForAllEncodings() throws Exception {
		String testString = "Hello";

		ByteBuffer utf8 = ByteBuffer.wrap(testString.getBytes("UTF-8"));
		ByteBuffer utf16 = ByteBuffer.wrap(testString.getBytes("UTF-16BE"));
		ByteBuffer utf16le = ByteBuffer.wrap(testString.getBytes("UTF-16LE"));
		ByteBuffer ascii = ByteBuffer.wrap(testString.getBytes("US-ASCII"));

		LiteralStringInstruction strInst = new LiteralStringInstruction("US-ASCII");
		strInst.setLiteral("Hello");
		strInst.evaluate(ascii);

		strInst.setEncoding("UTF-8");
		strInst.evaluate(utf8);

		strInst.setEncoding("UTF-16BE");
		strInst.evaluate(utf16);

		strInst.setEncoding("UTF-16LE");
		strInst.evaluate(utf16le);
	}

	@Test(expected=ExecutionException.class)
	public void testLiteralStringInstructionThrowsExecutionInstructionIfStringDoesNotMatch() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction("US-ASCII");
		unit.setLiteral("notData");
		unit.execute(testBuffer, ed);

		assertThat((String) unit.value, is("TestData!"));
	}

	@Test
	public void testTerminatedString() throws Exception {
		TerminatedString unit = new TerminatedString("US-ASCII");
		unit.address = -1;
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
}
