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
import com.revbingo.spiff.parser.ParseException;

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
			assertThat((Byte) unit.value, is(equalTo(testData[i])));
			assertThat(unit.address, is(equalTo(i)));
		}
	}

	@Test
	public void testBytesInstruction() throws Exception {
		BytesInstruction unit = new BytesInstruction();
		unit.setLengthExpr("5");
		unit.execute(testBuffer, ed, evaluator);

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
			unit.execute(testBuffer, ed, evaluator);
			assertThat((Short) unit.value, is(equalTo(expectedValues[i])));
			assertThat(unit.address, is(equalTo(i*2)));
		}
	}

	@Test
	public void testIntegerInstruction() throws Exception {
		IntegerInstruction unit = new IntegerInstruction();

		int[] expectedValues = new int[] { 0x54657374, 0x44617461 };

		for(int i = 0; i < 2; i++) {
			unit.execute(testBuffer, ed, evaluator);
			assertThat((Integer) unit.value, is(equalTo(expectedValues[i])));
			assertThat(unit.address, is(equalTo(i*4)));
		}
	}

	@Test
	public void testLongInstruction() throws Exception {
		ByteInstruction padder = new ByteInstruction();
		padder.execute(testBuffer, ed, evaluator);

		LongInstruction unit = new LongInstruction();

		long expectedValue = 0x6573744461746121L;

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Long) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(1)));
	}

	@Test
	public void testDoubleInstruction() throws Exception {
		DoubleInstruction unit = new DoubleInstruction();
		unit.address = -1;

		double expectedValue = 3.66552341002185E98d;

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Double) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testBitsInstruction() throws Exception {
		BitsInstruction unit = new BitsInstruction();
		String numberOfBitsExpression = "13";
		unit.setNumberOfBitsExpr(numberOfBitsExpression);
		unit.execute(testBuffer, ed, evaluator);

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

		unit.execute(testBuffer, ed, evaluator);
		assertThat((Float) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithExplicitEncoding() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.address = -1;

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.value, is(equalTo("Test")));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testFixedLengthStringWithNoExplicitEncodingDropsToASystemDefault() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");
		unit.address = -1;

		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.value, is(equalTo("Test")));

		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void fixedLengthStringIsTrimmed() throws Exception {
		FixedLengthString unit = new FixedLengthString("US-ASCII");

		byte[] paddedData = new byte[testData.length + 3];
		System.arraycopy(testData, 0, paddedData, 0, testData.length);

		ByteBuffer paddedBuffer = ByteBuffer.wrap(paddedData);
		unit.setLengthExpr("12");
		unit.execute(paddedBuffer, ed, evaluator);

		assertThat((String) unit.value, is(equalTo("TestData!")));
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
		unit.execute(testBuffer, ed, evaluator);

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
		strInst.evaluate(ascii, evaluator);

		strInst.setEncoding("UTF-8");
		strInst.evaluate(utf8, evaluator);

		strInst.setEncoding("UTF-16BE");
		strInst.evaluate(utf16, evaluator);

		strInst.setEncoding("UTF-16LE");
		strInst.evaluate(utf16le, evaluator);
	}

	@Test(expected=ExecutionException.class)
	public void testLiteralStringInstructionThrowsExecutionInstructionIfStringDoesNotMatch() throws Exception {
		LiteralStringInstruction unit = new LiteralStringInstruction("US-ASCII");
		unit.setLiteral("notData");
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.value, is("TestData!"));
	}

	@Test
	public void testTerminatedString() throws Exception {
		TerminatedString unit = new TerminatedString("US-ASCII");
		unit.address = -1;
		unit.execute(testBuffer, ed, evaluator);

		assertThat((String) unit.value, is(equalTo("TestData!")));
		assertThat(unit.address, is(equalTo(0)));
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
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF });
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Short) unit.value, is(equalTo((short) 0xFF)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithLittleEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Integer) unit.value, is(equalTo(0xFF00)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedShortInstructionWithBigEndianOrder() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0x00, (byte) 0xFF });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Integer) unit.value, is(equalTo(0x00FF)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithBigEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.BIG_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Long) unit.value, is(equalTo((long) 0xCAFEBABE)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test
	public void testUnsignedIntegerInstructionWithLittleEndianOrder() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.address = -1;

		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE });
		unsignedByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		unit.execute(unsignedByteBuffer, ed, evaluator);
		assertThat((Long) unit.value, is(equalTo((long) 0xBEBAFECA)));
		assertThat(unit.address, is(equalTo(0)));
	}

	@Test(expected=ParseException.class)
	public void numberFactoryThrowsExceptionForUnknownType() throws Exception {
		FixedLengthNumberFactory unit = new FixedLengthNumberFactory();
		unit.getInstruction("oops");
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
}
