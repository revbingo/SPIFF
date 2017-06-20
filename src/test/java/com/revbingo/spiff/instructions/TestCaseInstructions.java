package com.revbingo.spiff.instructions;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.IntegerInstruction;
import com.revbingo.spiff.evaluator.EvalExEvaluator;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestCaseInstructions {

	ByteBuffer testBuffer;
	byte[] testData;

	Mockery context = new Mockery();
	EventListener ed;
	Evaluator evaluator;

	@Before
	public void setUp() {
		testData = new byte[] { 0x54,0x65,0x73,0x74,0x44,0x61,0x74,0x61,0x21,0x00 };
		testBuffer = ByteBuffer.wrap(testData);
		evaluator = new EvalExEvaluator();
		ed = context.mock(EventListener.class);
		final EventListener dispatcher = ed;
		context.checking(new Expectations() {{
			ignoring(dispatcher).notifyData(with(any(Datatype.class)));
			ignoring(dispatcher).notifyGroup(with(any(String.class)), with(any(boolean.class)));
		}});
	}

	@Test
	public void testSetOrderInstruction() throws Exception {
		SetOrderInstruction unit = new SetOrderInstruction(ByteOrder.LITTLE_ENDIAN);
		unit.execute(testBuffer, ed, evaluator);

		IntegerInstruction test = new IntegerInstruction("aName");
		test.execute(testBuffer, ed, evaluator);

		assertThat((Integer) test.getValue(), is(equalTo(0x74736554)));

		unit = new SetOrderInstruction(ByteOrder.BIG_ENDIAN);
		unit.execute(testBuffer, ed, evaluator);

		test = new IntegerInstruction("aName");
		test.execute(testBuffer, ed, evaluator);

		assertThat((Integer) test.getValue(), is(equalTo(0x44617461)));
	}

	@Test
	public void testRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final Datatype theInstruction = new ByteInstruction("aName");

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock unit = new RepeatBlock("10", Collections.singletonList(theInstruction));

		unit.execute(testBuffer, dispatcher, evaluator);

		context.assertIsSatisfied();
	}

	@Test
	public void testNestedRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final Datatype theInstruction = new ByteInstruction("aName");

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock innerUnit = new RepeatBlock("2", Collections.singletonList(theInstruction));
		RepeatBlock outerUnit = new RepeatBlock("5", Collections.singletonList(innerUnit));

		outerUnit.execute(testBuffer, dispatcher, evaluator);

		context.assertIsSatisfied();
	}

	@Test
	public void testMarkInstruction() throws Exception {
		MarkInstruction unit = new MarkInstruction("testMark");

		IntegerInstruction previousInstruction = new IntegerInstruction("aName");
		previousInstruction.execute(testBuffer, ed, evaluator);
		unit.execute(testBuffer, ed, evaluator);

		assertThat(evaluator.evaluate("testMark", Integer.TYPE), is(equalTo(4)));
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
		unitStart.execute(testBuffer, dispatcher, evaluator);
		unitEnd.execute(testBuffer, dispatcher, evaluator);

		context.assertIsSatisfied();
	}

	@Test
	public void jumpInstruction() throws Exception {
		int positionToJumpTo = 4;
		JumpInstruction unit = new JumpInstruction(String.valueOf(positionToJumpTo));
		ByteInstruction nextInst = new ByteInstruction("aName");

		unit.execute(testBuffer, ed, evaluator);
		nextInst.execute(testBuffer, ed, evaluator);

		assertThat((Byte) nextInst.getValue(), is(testData[positionToJumpTo]));
		assertThat(testBuffer.position(), is(positionToJumpTo + 1));
	}

	@Test
	public void setInstructionAddsExpressionResultToEvaluator() throws Exception {
		try {
			evaluator.evaluate("theResult", Integer.TYPE);
			fail("Should not have been able to resolve theResult");
		} catch (ExecutionException e) {}

		SetInstruction unit = new SetInstruction("8", "theResult");

		unit.execute(testBuffer, ed, evaluator);

		assertThat(evaluator.evaluate("theResult", Integer.TYPE), is(8));
	}

	@Test
	public void ifBlockRunsIfInstructionsWhenExpressionIsTrue() throws Exception {
		ByteInstruction ifInst = new ByteInstruction("aName");
		List<Instruction> ifInsts = Collections.singletonList((Instruction) ifInst);

		JumpInstruction elseInst1 = new JumpInstruction("3");
		ByteInstruction elseInst2 = new ByteInstruction("aName");
		List<Instruction> elseInsts = Arrays.asList((Instruction) elseInst1, elseInst2);

		IfBlock unit = new IfBlock("1 == 1", ifInsts, new Block(elseInsts));

		unit.execute(testBuffer, ed, evaluator);

		assertThat((Byte) ifInst.getValue(), is((byte) 0x54));
		assertThat(elseInst2.getValue(), is(nullValue()));
	}

	@Test
	public void ifBlockRunsElseInstructionsWhenExpressionIsFalse() throws Exception {
		ByteInstruction ifInst = new ByteInstruction("aName");
		List<Instruction> ifInsts = Collections.singletonList((Instruction) ifInst);

		JumpInstruction elseInst1 = new JumpInstruction("3");
		ByteInstruction elseInst2 = new ByteInstruction("aName");
		List<Instruction> elseInsts = Arrays.asList((Instruction) elseInst1, elseInst2);

		IfBlock unit = new IfBlock("1 != 1", ifInsts, new Block(elseInsts));

		unit.execute(testBuffer, ed, evaluator);

		assertThat((Byte) elseInst2.getValue(), is((byte) 0x74));
		assertThat(ifInst.getValue(), is(nullValue()));
	}

	@Test
	public void skipInstructionSkipsSpecifiedNumberOfBytes() {
		SkipInstruction skipInst = new SkipInstruction("2");

		skipInst.execute(testBuffer, ed, evaluator);

		assertThat(testBuffer.position(), is(2));
	}
}
