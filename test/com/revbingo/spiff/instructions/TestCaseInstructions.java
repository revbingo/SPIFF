package com.revbingo.spiff.instructions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;

public class TestCaseInstructions {

	ByteBuffer testBuffer;
	byte[] testData;
	
	MockEventDispatcher ed;
	
	@Before
	public void setUp() {
		testData = new byte[] { 0x54,0x65,0x73,0x74,0x44,0x61,0x74,0x61,0x21,0x00 };
		testBuffer = ByteBuffer.wrap(testData);
		ed = new MockEventDispatcher();
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
	public void testFloatInstruction() throws Exception {
		FloatInstruction unit = new FloatInstruction();
		unit.address = -1;
		
		float expectedValue = 3.94193797E12f;
		
		unit.execute(testBuffer, ed);
		assertThat((Float) unit.value, is(equalTo(expectedValue)));
		assertThat(unit.address, is(equalTo(0)));
	}
	
	@Test
	public void testFixedLengthString() throws Exception {
		FixedLengthString unit = new FixedLengthString();
		unit.address = -1;
		
		unit.setEncoding("US-ASCII");
		unit.setLengthExpr("4");
		unit.execute(testBuffer, ed);
		
		assertThat((String) unit.value, is(equalTo("Test")));
		assertThat(unit.address, is(equalTo(0)));
	}
	
	@Test
	public void testTerminated() throws Exception {
		TerminatedString unit = new TerminatedString();
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
	public void testUnsignedShortInstruction() throws Exception {
		UnsignedShortInstruction unit = new UnsignedShortInstruction();
		unit.address = -1;
		
		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF, (byte) 0xFF });
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Integer) unit.value, is(equalTo((int) 0xFFFF)));
		assertThat(unit.address, is(equalTo(0)));
	}
	
	@Test
	public void testUnsignedIntegerInstruction() throws Exception {
		UnsignedIntegerInstruction unit = new UnsignedIntegerInstruction();
		unit.address = -1;
		
		ByteBuffer unsignedByteBuffer = ByteBuffer.wrap(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF });
		unit.execute(unsignedByteBuffer, ed);
		assertThat((Long) unit.value, is(equalTo((long) 0xFFFFFFFF)));
		assertThat(unit.address, is(equalTo(0)));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUnsignedLongIsNotSupported() throws Exception {
		new UnsignedLongInstruction().execute(testBuffer, ed);
	}
	
	@Test
	public void testRepeatBlock() throws Exception {
		RepeatBlock unit = new RepeatBlock();
		unit.setInstructions(Arrays.asList(new Instruction[] { new ByteInstruction() }));
		unit.setRepeatCountExpression("10");
		
		unit.execute(testBuffer, ed);
		
		assertThat(ed.receivedInstructions.size(), is(equalTo(10)));
	}
	

	@Test
	public void testNestedRepeatBlock() throws Exception {
		RepeatBlock outerUnit = new RepeatBlock();
		outerUnit.setRepeatCountExpression("5");
		RepeatBlock innerUnit = new RepeatBlock();
		innerUnit.setInstructions(Arrays.asList((Instruction) new ByteInstruction()));
		innerUnit.setRepeatCountExpression("2");
		
		outerUnit.setInstructions(Arrays.asList(new Instruction[] { innerUnit }));
		outerUnit.execute(testBuffer, ed);
		
		assertThat(ed.receivedInstructions.size(), is(equalTo(10)));
	}
	
	@Test
	public void testMarkInstruction() throws Exception {
		MarkInstruction unit = new MarkInstruction();
		unit.setName("testMark");
		
		IntegerInstruction intIns = new IntegerInstruction();
		intIns.execute(testBuffer, ed);
		unit.execute(testBuffer, ed);
		
		assertThat(Evaluator.evaluateInt("testMark"), is(equalTo(4)));
	}
	
	public class MockEventDispatcher implements EventDispatcher {

		public List<ReferencedInstruction> receivedInstructions = new ArrayList<ReferencedInstruction>();

		@Override
		public void notifyData(ReferencedInstruction ins) {
			receivedInstructions.add(ins);
		}

		@Override
		public void notifyGroup(String groupName, boolean start) {
			
		}
		
	}
}
