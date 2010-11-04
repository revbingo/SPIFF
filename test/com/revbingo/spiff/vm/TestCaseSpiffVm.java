package com.revbingo.spiff.vm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.datatypes.ByteInstruction;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;

public class TestCaseSpiffVm {

	@Test
	public void canConstructVmWithListOfInstructionsAndByteBufferAndEventDispatcher() {
		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(new ByteInstruction("a"));
		TestEventListener ed = new TestEventListener();
		SpiffVm unit = new SpiffVm(instructions, ByteBuffer.wrap(new byte[] { 0x7f }), ed);
		unit.start();
		assertThat(ed.lastInstruction.getName(), is("a"));
	}

	@Test
	public void evaluatorIsInitialisedWithLengthOfFileAsSpecialVar() {
		SpiffVm unit = new SpiffVm(null, ByteBuffer.wrap(new byte[] { 0x00, 0x00, 0x00, 0x00 }), null);
		assertThat((Integer) unit.getVar("fileLength"), is(4));
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
}
