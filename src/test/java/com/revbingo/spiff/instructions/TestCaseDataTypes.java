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
package com.revbingo.spiff.instructions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.BitsInstruction;
import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.BytesInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.DoubleInstruction;
import com.revbingo.spiff.datatypes.FixedLengthString;
import com.revbingo.spiff.datatypes.FloatInstruction;
import com.revbingo.spiff.datatypes.IntegerInstruction;
import com.revbingo.spiff.datatypes.LiteralStringInstruction;
import com.revbingo.spiff.datatypes.LongInstruction;
import com.revbingo.spiff.datatypes.ShortInstruction;
import com.revbingo.spiff.datatypes.TerminatedString;
import com.revbingo.spiff.datatypes.UnsignedByteInstruction;
import com.revbingo.spiff.datatypes.UnsignedIntegerInstruction;
import com.revbingo.spiff.datatypes.UnsignedShortInstruction;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public class TestCaseDataTypes {
	ByteBuffer testBuffer;
	byte[] testData;

	Mockery context = new Mockery();
	EventListener ed;
	Evaluator evaluator;

	@Before
	public void setUp() {
		testData = new byte[] { 0x54,0x65,0x73,0x74,0x44,0x61,0x74,0x61,0x21,0x00 };
		testBuffer = ByteBuffer.wrap(testData);
		ed = context.mock(EventListener.class);
		evaluator = new Evaluator();
		final EventListener dispatcher = ed;
		context.checking(new Expectations() {{
			ignoring(dispatcher).notifyData(with(any(Datatype.class)));
			ignoring(dispatcher).notifyGroup(with(any(String.class)), with(any(boolean.class)));
		}});
	}

	@Test
	public void testByteInstruction() throws Exception {
		ByteInstruction unit = new ByteInstruction();

		for(int i = 0; i < 10; i++ ) {
			unit.execute(testBuffer, ed, evaluator);
			assertThat((Byte) unit.getValue(), is(equalTo(testData[i])));
			assertThat(unit.getAddress(), is(equalTo(i)));
		}
	}

	@Test
	public void testBytesInstruction() throws Exception {
		BytesInstruction unit = new BytesInstruction();
		unit.setLengthExpr("5");
		unit.execute(testBuffer, ed, evaluator);

		assertThat(unit.getValue(), instanceOf(byte[].class));
		byte[] bytes = (byte[]) unit.getValue();
		assertThat(bytes.length, is(5));
		assertThat(bytes, is(new byte[] { 0x54,0x65,0x73,0x74,0x44}));
		assertThat(testBuffer.position(), is(5));
	}

	@Test
	public void testShortInstruction() throws Exception {
		ShortInstruction unit = new ShortInstruction();

		short[] expectedValues = new short[] { 0x5465, 0x7374, 0x4461, 0x7461, 0x2100 };

		for(int i = 0; i < 5; i++) {
			unit.execute(testBuffer, ed, evaluator);
			assertThat((Short) unit.getValue(), is(equalTo(expectedValues[i])));
			assertThat(unit.getAddress(), is(equalTo(i*2)));
		}
	}

	@Test
	public void testIntegerInstruction() throws Exception {
		IntegerInstruction unit = new IntegerInstruction();

		int[] expectedValues = new int[] { 0x54657374, 0x44617461 };

		for(int i = 0; i < 2; i++) {
			unit.execute(testBuffer, ed, evaluator);
			assertThat((Integer) unit.getValue(), is(equalTo(expectedValues[i])));
			assertThat(unit.getAddress(), is(equalTo(i*4)));
		}
	}

	@Test
	public void testLongInstruction() throws Exception {
		ByteInstruction padder = new ByteInstruction();
		padder.execute(testBuffer, ed, evaluator);

		LongInstruction unit = new LongInstruction();

		long expectedValue = 0x6573744461746121L;

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Long) unit.getValue(), is(equalTo(expectedValue)));
		assertThat(unit.getAddress(), is(equalTo(1)));
	}

	@Test
	public void testDoubleInstruction() throws Exception {
		DoubleInstruction unit = new DoubleInstruction();
		unit.setAddress(-1);

		double expectedValue = 3.66552341002185E98d;

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Double) unit.getValue(), is(equalTo(expectedValue)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testBitsInstruction() throws Exception {
		BitsInstruction unit = new BitsInstruction();
		String numberOfBitsExpression = "13";
		unit.setNumberOfBitsExpr(numberOfBitsExpression);
		unit.execute(testBuffer, ed, evaluator);

		assertThat(unit.getValue(), instanceOf(boolean[].class));

		boolean[] result = (boolean[]) unit.getValue();
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
		unit.setAddress(-1);

		float expectedValue = 3.94193797E12f;

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Float) unit.getValue(), is(equalTo(expectedValue)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithExplicitEncoding() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.setAddress(-1);

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is(equalTo("Test")));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithNoExplicitEncodingDropsToASystemDefault() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.setAddress(-1);

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is(equalTo("Test")));

		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void fixedLengthStringIsTrimmed() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");

		byte[] paddedData = new byte[testData.length + 3];
		System.arraycopy(testData, 0, paddedData, 0, testData.length);

		ByteBuffer paddedBuffer = ByteBuffer.wrap(paddedData);
		unit.setLengthExpr("12");
		unit.execute(paddedBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is(equalTo("TestData!")));
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
		LiteralStringInstruction unit = new LiteralStringInstruction("TestData!", "US-ASCII");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is("TestData!"));
	}

	@Test
	public void literalStringWorksForAllEncodings() throws Exception {
		String testString = "Hello";

		ByteBuffer utf8 = ByteBuffer.wrap(testString.getBytes("UTF-8"));
		ByteBuffer utf16 = ByteBuffer.wrap(testString.getBytes("UTF-16BE"));
		ByteBuffer utf16le = ByteBuffer.wrap(testString.getBytes("UTF-16LE"));
		ByteBuffer ascii = ByteBuffer.wrap(testString.getBytes("US-ASCII"));

		LiteralStringInstruction strInstAscii = new LiteralStringInstruction("Hello", "US-ASCII");
		strInstAscii.evaluate(ascii, evaluator);

		LiteralStringInstruction strInstUtf8 = new LiteralStringInstruction("Hello", "UTF-8");
		strInstUtf8.evaluate(utf8, evaluator);

		LiteralStringInstruction strInstUtf16 = new LiteralStringInstruction("Hello", "UTF-16BE");
		strInstUtf16.evaluate(utf16, evaluator);

		LiteralStringInstruction strInstUtf16LE = new LiteralStringInstruction("Hello", "UTF-16LE");
		strInstUtf16LE.evaluate(utf16le, evaluator);
	}

	@Test(expected=ExecutionException.class)
	public void testLiteralStringInstructionThrowsExecutionInstructionIfStringDoesNotMatch() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction("notData", "US-ASCII");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is("TestData!"));
	}

	@Test
	public void testTerminatedString() throws Exception {
		TerminatedString unit = new TerminatedString("US-ASCII");
		unit.setAddress(-1);
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.getValue(), is(equalTo("TestData!")));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test(expected=BufferUnderflowException.class)
	public void testPastEndOfBufferThrowsException() throws ExecutionException {
		LongInstruction unit = new LongInstruction();

		unit.execute(testBuffer, ed, evaluator);
		unit.execute(testBuffer, ed, evaluator);
	}

	@Test
	public void testUnsignedByteInstruction() throws Exception {
		UnsignedByteInstruction unit = new UnsignedByteInstruction();
		unit.setAddress(-1);

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xE7 });
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Short) unit.getValue(), is(equalTo((short) 231)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithLittleEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.setAddress(-1);

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Integer) unit.getValue(), is(equalTo(0xFF00)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithBigEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.setAddress(-1);

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Integer) unit.getValue(), is(equalTo(0x00FF)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithBigEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.setAddress(-1);

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Long) unit.getValue(), is(equalTo((long) 0xCAFEBABE)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithLittleEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.setAddress(-1);

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Long) unit.getValue(), is(equalTo((long) 0xBEBAFECA)));
		assertThat(unit.getAddress(), is(equalTo(0)));
	}

	@Test
	public void referencedInstructionStoresAddressWhenExecuted() throws Exception {
		IntegerInstruction previousInstruction = new IntegerInstruction();
		Datatype unit = new Datatype() {
			@Override
			public Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException {
				return null;
			}
		};

		previousInstruction.execute(testBuffer, ed, evaluator);
		unit.execute(testBuffer, ed, evaluator);

		assertThat(unit.getAddress(), is(4));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfByteLiteralNumberDoesNotMatch() throws Exception {
		ByteInstruction inst = new ByteInstruction();
		inst.setLiteralExpr("0x54");
	
		try {
			//first byte should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next byte should fail
		inst.execute(testBuffer, ed, evaluator);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfShortLiteralNumberDoesNotMatch() throws Exception {
		ShortInstruction inst = new ShortInstruction();
		inst.setLiteralExpr("0x5465");
	
		try {
			//first short should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next short should fail
		inst.execute(testBuffer, ed, evaluator);
	}

	@Test(expected=ExecutionException.class)
	public void exceptionIfIntegerLiteralNumberDoesNotMatch() throws Exception {
		IntegerInstruction inst = new IntegerInstruction();
		inst.setLiteralExpr("0x54657374");
	
		try {
			//first int should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next int should fail
		inst.execute(testBuffer, ed, evaluator);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfLongLiteralNumberDoesNotMatch() throws Exception {
		byte[] testData = new byte[] { 0x54,0x65,0x73,0x74,0x44,0x61,0x74,0x61,0x21,0x00,0x00,0x00,0x00,0x00,0x00,0x00 };
		ByteBuffer longerBuffer = ByteBuffer.wrap(testData);
		
		LongInstruction inst = new LongInstruction();
		inst.setLiteralExpr("0x5465737444617461L");
		
		try {
			//first long should pass
			inst.execute(longerBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next long should fail
		inst.execute(longerBuffer, ed, evaluator);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfUnsignedByteLiteralNumberDoesNotMatch() throws Exception {
		UnsignedByteInstruction inst = new UnsignedByteInstruction();
		inst.setLiteralExpr("0x54");
		
		try {
			//first byte should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next byte should fail
		inst.execute(testBuffer, ed, evaluator);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfUnsignedShortLiteralNumberDoesNotMatch() throws Exception {
		UnsignedShortInstruction inst = new UnsignedShortInstruction();
		inst.setLiteralExpr("0x5465");
		
		try {
			//first short should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}
		
		//next short should fail
		inst.execute(testBuffer, ed, evaluator);
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionIfUnsignedIntegerLiteralNumberDoesNotMatch() throws Exception {
		UnsignedIntegerInstruction inst = new UnsignedIntegerInstruction();
		inst.setLiteralExpr("0x54657374");
		
		try {
			//first int should pass
			inst.execute(testBuffer, ed, evaluator);
		} catch(Exception e) {
			fail();
		}

		//next int should fail
		inst.execute(testBuffer, ed, evaluator);
	}
}
