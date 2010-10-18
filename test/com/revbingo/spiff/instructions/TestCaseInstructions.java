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
