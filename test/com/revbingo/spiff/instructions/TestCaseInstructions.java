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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.IntegerInstruction;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

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
		evaluator = new Evaluator();
		ed = context.mock(EventListener.class);
		final EventListener dispatcher = ed;
		context.checking(new Expectations() {{
			ignoring(dispatcher).notifyData(with(any(Datatype.class)));
			ignoring(dispatcher).notifyGroup(with(any(String.class)), with(any(boolean.class)));
		}});
	}

	@Test
	public void testSetOrderInstruction() throws Exception {
		SetOrderInstruction unit = new SetOrderInstruction();

		unit.setOrder(ByteOrder.LITTLE_ENDIAN);
		unit.execute(testBuffer, ed, evaluator);

		IntegerInstruction test = new IntegerInstruction();
		test.execute(testBuffer, ed, evaluator);

		assertThat((Integer) test.value, is(equalTo(0x74736554)));

		unit.setOrder(ByteOrder.BIG_ENDIAN);
		unit.execute(testBuffer, ed, evaluator);

		test = new IntegerInstruction();
		test.execute(testBuffer, ed, evaluator);

		assertThat((Integer) test.value, is(equalTo(0x44617461)));
	}

	@Test
	public void testRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final Datatype theInstruction = new ByteInstruction();

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock unit = new RepeatBlock();
		unit.setInstructions(Arrays.asList((Instruction) theInstruction));
		unit.setRepeatCountExpression("10");

		unit.execute(testBuffer, dispatcher, evaluator);

		context.assertIsSatisfied();
	}

	@Test
	public void testNestedRepeatBlock() throws Exception {
		context = new Mockery();

		final EventListener dispatcher = context.mock(EventListener.class);
		final Datatype theInstruction = new ByteInstruction();

		context.checking(new Expectations() {{
			exactly(10).of(dispatcher).notifyData(with(theInstruction));
		}});

		RepeatBlock outerUnit = new RepeatBlock();
		outerUnit.setRepeatCountExpression("5");
		RepeatBlock innerUnit = new RepeatBlock();
		innerUnit.setInstructions(Arrays.asList((Instruction) theInstruction));
		innerUnit.setRepeatCountExpression("2");

		outerUnit.setInstructions(Arrays.asList(new Instruction[] { innerUnit }));
		outerUnit.execute(testBuffer, dispatcher, evaluator);

		context.assertIsSatisfied();
	}

	@Test
	public void testMarkInstruction() throws Exception {
		MarkInstruction unit = new MarkInstruction();
		unit.setName("testMark");

		IntegerInstruction previousInstruction = new IntegerInstruction();
		previousInstruction.execute(testBuffer, ed, evaluator);
		unit.execute(testBuffer, ed, evaluator);

		assertThat(evaluator.evaluateInt("testMark"), is(equalTo(4)));
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
		unitStart.execute(testBuffer, dispatcher, null);
		unitEnd.execute(testBuffer, dispatcher, null);

		context.assertIsSatisfied();
	}

	@Test
	public void blockCanBeUsedAsIterable() throws Exception {
		List<Instruction> insts = Arrays.asList((Instruction) new ByteInstruction("one"), new ByteInstruction("two"));

		Block unit = new Block();
		unit.setInstructions(insts);

		int count = 0;
		for(Instruction i : unit) {
			count++;
		}
		assertThat(count, is(2));
	}

	@Test
	public void jumpInstruction() throws Exception {
		int positionToJumpTo = 4;
		JumpInstruction unit = new JumpInstruction();
		unit.setExpression(String.valueOf(positionToJumpTo));
		ByteInstruction nextInst = new ByteInstruction();

		unit.execute(testBuffer, ed, evaluator);
		nextInst.execute(testBuffer, ed, evaluator);

		assertThat((Byte) nextInst.value, is(testData[positionToJumpTo]));
		assertThat(testBuffer.position(), is(positionToJumpTo + 1));
	}

	@Test
	public void setInstructionAddsExpressionResultToEvaluator() throws Exception {
		try {
			evaluator.evaluateInt("theResult");
			fail("Should not have been able to resolve theResult");
		} catch (ExecutionException e) {}

		SetInstruction unit = new SetInstruction();
		unit.setExpression("8");
		unit.setVarname("theResult");

		unit.execute(testBuffer, ed, evaluator);

		assertThat(evaluator.evaluateInt("theResult"), is(8));
	}

	@Test
	public void ifBlockRunsIfInstructionsWhenExpressionIsTrue() throws Exception {
		ByteInstruction ifInst = new ByteInstruction();
		List<Instruction> ifInsts = Arrays.asList((Instruction) ifInst);

		JumpInstruction elseInst1 = new JumpInstruction();
		elseInst1.setExpression("3");
		ByteInstruction elseInst2 = new ByteInstruction();
		List<Instruction> elseInsts = Arrays.asList((Instruction) elseInst1, elseInst2);

		IfBlock unit = new IfBlock();
		unit.setIfExpression("1 == 1");
		unit.setInstructions(ifInsts);
		unit.setElseInstructions(elseInsts);

		unit.execute(testBuffer, ed, evaluator);

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
		List<Instruction> elseInsts = Arrays.asList((Instruction) elseInst1, elseInst2);

		IfBlock unit = new IfBlock();
		unit.setIfExpression("1 != 1");
		unit.setInstructions(ifInsts);
		unit.setElseInstructions(elseInsts);

		unit.execute(testBuffer, ed, evaluator);

		assertThat((Byte) elseInst2.value, is((byte) 0x74));
		assertThat(ifInst.value, is(nullValue()));
	}
}
