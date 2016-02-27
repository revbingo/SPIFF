/*
 * Copyright Mark Piper 2016
 *
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
 */
package com.revbingo.spiff.vm;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.parser.AdfFile;
import com.revbingo.spiff.parser.SpiffParser;

public class TestCaseSpiffVm {

	final List<Instruction> NO_INSTRUCTIONS = Collections.emptyList();

	private ByteInstruction byteNamed(String name) {
		ByteInstruction inst = new ByteInstruction();
		inst.setName(name);
		return inst;
	}

	@Test
	public void canConstructVmWithListOfInstructionsAndByteBufferAndEventDispatcher() {
		List<Instruction> instructions = new ArrayList<>();
		instructions.add(byteNamed("a"));
		TestEventListener ed = new TestEventListener();
		SpiffVm unit = new SpiffVm(instructions, ByteBuffer.wrap(new byte[] { 0x7f }), ed);
		unit.start();
		assertThat(ed.lastInstruction.getName(), is("a"));
	}

	@Test
	public void evaluatorIsInitialisedWithLengthOfFileAsSpecialVar() {
		SpiffVm unit = new SpiffVm(NO_INSTRUCTIONS, ByteBuffer.wrap(new byte[] { 0x00, 0x00, 0x00, 0x00 }), new NullEventListener());
		assertThat((Integer) unit.getVar("fileLength"), is(4));
	}

//	@Test
//	public void vmCanStepThroughAndReportLineNumber() throws Exception {
//		AdfFile adf = AdfFile.start()
//			.add("byte byteOne")
//			.add("byte byteTwo")
//			.end();
//		List<Instruction> instructions = new SpiffParser(adf.asInputStream()).parse();
//		TestEventListener ed = new TestEventListener();
//		SpiffVm unit = new SpiffVm(instructions,ByteBuffer.wrap(new byte[] { 0x11, 0x12}), ed);
//		runInThread(unit, true);
//		waitForSuspension(unit);
//
//		assertThat(unit.isSuspended(), is(true));
//		assertThat(unit.getNextLineNumber(), is(1));
//
//		unit.step();
//
//		assertThat(ed.lastInstruction.name, is("byteOne"));
//		assertThat((Byte) ed.lastInstruction.value, is((byte) 0x11));
//		assertThat(unit.getNextLineNumber(), is(2));
//
//		unit.step();
//		assertThat(ed.lastInstruction.name, is("byteTwo"));
//		assertThat((Byte) ed.lastInstruction.value, is((byte) 0x12));
//	}

	@Test
	public void canAddBreakpointAndVmStopsBeforeExecutingItAndThenResumes() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte byteOne")
			.add("byte byteTwo")
			.end();

		List<Instruction> instructions = new SpiffParser(adf.asInputStream()).parse();
		TestEventListener ed = new TestEventListener();
		SpiffVm unit = new SpiffVm(instructions,ByteBuffer.wrap(new byte[] { 0x11, 0x12}), ed);

		unit.setBreakpoint(2);

		VmThread thread = new VmThread(unit);
		thread.start();
		thread.waitForHalt();

		assertThat(ed.lastInstruction.getName(), is("byteOne"));
		assertThat(unit.getNextLineNumber(), is(2));

		thread.runToNext();

		assertThat(ed.lastInstruction.getName(), is("byteTwo"));
		assertThat(thread.isAlive(), is(false));
	}

	@Test
	@Ignore
	public void canAddBreakpointInALoopAndSeeMultipleHalts() throws Exception {
		AdfFile adf = AdfFile.start()
			.add(".repeat(3) {")
			.add("	byte theByte")
			.add("}")
			.end();

		List<Instruction> instructions = new SpiffParser(adf.asInputStream()).parse();
		TestEventListener ed = new TestEventListener();
		SpiffVm unit = new SpiffVm(instructions,ByteBuffer.wrap(new byte[] { 0x11, 0x22, 0x33}), ed);

		unit.setBreakpoint(2);

		VmThread thread = new VmThread(unit);
		thread.start();
		thread.waitForHalt();

		assertThat(ed.lastInstruction, is(nullValue()));

		thread.runToNext();

		assertThat(ed.lastInstruction, is(not(nullValue())));
		assertThat(ed.lastInstruction.getName(), is("theByte"));
		assertThat((Byte) ed.lastInstruction.getValue(), is((byte) 0x11));

		thread.runToNext();

		assertThat(ed.lastInstruction, is(not(nullValue())));
		assertThat(ed.lastInstruction.getName(), is("theByte"));
		assertThat((Byte) ed.lastInstruction.getValue(), is((byte) 0x22));

		thread.runToNext();

		assertThat(ed.lastInstruction, is(not(nullValue())));
		assertThat(ed.lastInstruction.getName(), is("theByte"));
		assertThat((Byte) ed.lastInstruction.getValue(), is((byte) 0x33));
	}

	public class TestEventListener implements EventListener {

		public Datatype lastInstruction;

		@Override
		public void notifyData(Datatype ins) {
			lastInstruction = ins;
		}

		@Override
		public void notifyGroup(String groupName, boolean start) {
		}

	}

	private class VmThread extends Thread {

		private SpiffVm vm;

		public VmThread(SpiffVm unit) {
			this.vm = unit;
		}

		public void runToNext() throws Exception {
			vm.resume();
			waitForHalt();
		}

		@Override
		public void run() {
			vm.start();
		}

		public void waitForHalt() throws Exception {
			do {
				Thread.sleep(100);
			} while(this.isAlive() && !vm.isSuspended());
		}

	}
}
