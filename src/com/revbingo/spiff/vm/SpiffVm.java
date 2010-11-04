package com.revbingo.spiff.vm;

import java.nio.ByteBuffer;
import java.util.List;

import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;

public class SpiffVm {

	private List<Instruction> instructions;
	private ByteBuffer buffer;
	private EventListener ed;
	private Evaluator evaluator = new Evaluator();

	public SpiffVm(List<Instruction> instructions, ByteBuffer buffer,
			EventListener ed) {
		this.instructions = instructions;
		this.buffer = buffer;
		this.ed = ed;

		evaluator.addVariable("fileLength", buffer.limit());
	}

	public void start() {
		for(Instruction i : instructions) {
			i.execute(buffer, ed, evaluator);
		}
	}

	public Object getVar(String expression) {
		return evaluator.evaluate(expression);
	}
}
