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
import com.revbingo.spiff.parser.AdfFile;
import com.revbingo.spiff.parser.SpiffParser;

public class TestCaseSpiffVm {

	@Test
	public void canConstructVmWithListOfInstructionsAndByteBufferAndEventDispatcher() {
		List<Instruction> instructions = new ArrayList<Instruction>();
		instructions.add(new ByteInstruction("a"));
		TestEventListener ed = new TestEventListener();
		SpiffVm unit = new SpiffVm(instructions, ByteBuffer.wrap(new byte[] { 0x7f }), ed);
		unit.start(false);
		assertThat(ed.lastInstruction.getName(), is("a"));
	}

	@Test
	public void evaluatorIsInitialisedWithLengthOfFileAsSpecialVar() {
		SpiffVm unit = new SpiffVm(null, ByteBuffer.wrap(new byte[] { 0x00, 0x00, 0x00, 0x00 }), null);
		assertThat((Integer) unit.getVar("fileLength"), is(4));
	}

	@Test
	public void vmCanStepThroughAndReportLineNumber() throws Exception {
		AdfFile adf = AdfFile.start()
			.add("byte byteOne")
			.add("byte byteTwo")
			.end();
		List<Instruction> instructions = new SpiffParser(adf.asInputStream()).parse();
		SpiffVm unit = new SpiffVm(instructions,ByteBuffer.wrap(new byte[] { 0x01, 0x02}), new TestEventListener());
		runInThread(unit, true);
		waitForSuspension(unit);

		assertThat(unit.isSuspended(), is(true));
		assertThat(unit.getNextLineNumber(), is(1));

		unit.step();

		assertThat(unit.getNextLineNumber(), is(2));
	}

	private void runInThread(final SpiffVm vm, final boolean step) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				vm.start(step);
			}
		};

		Thread t = new Thread(r);
		t.start();
	}

	private void waitForSuspension(SpiffVm vm) {
		do {
			try { Thread.sleep(100); } catch(Exception e) {}
		} while(!vm.isSuspended());
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
