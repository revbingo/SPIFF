package com.revbingo.spiff.vm;

import java.nio.ByteBuffer;
import java.util.List;

import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.VmInstruction;

public class SpiffVm {

	private List<Instruction> instructions;
	private ByteBuffer buffer;
	private EventListener ed;
	private Evaluator evaluator = new Evaluator();
	private int programCounter;
	private boolean isStepping;
	private boolean isSuspended;
	private Object suspendedLock = new Object();
	private Object stepLock = new Object();

	public SpiffVm(List<Instruction> instructions, ByteBuffer buffer,
			EventListener ed) {
		this.instructions = instructions;
		this.buffer = buffer;
		this.ed = ed;

		evaluator.addVariable("fileLength", buffer.limit());
	}

	public void start(boolean step) {
		isStepping = step;
		for(Instruction i : instructions) {
			if(i instanceof VmInstruction) {
				programCounter = ((VmInstruction) i).lineNumber;
			}

			if(isStepping) {
				try {
					synchronized (suspendedLock) {
						isSuspended = true;
						suspendedLock.notify();
					}
					synchronized (stepLock) {
						stepLock.wait();
					}
				} catch (InterruptedException e) {
				} finally {
					isSuspended = false;
				}
			}
			i.execute(buffer, ed, evaluator);
		}
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public Object getVar(String expression) {
		return evaluator.evaluate(expression);
	}

	public Integer getNextLineNumber() {
		return programCounter;
	}

	public void step() {
		isStepping = true;

		synchronized (stepLock) {
			stepLock.notify();
		}

		synchronized (suspendedLock) {
			try {
				suspendedLock.wait();
			} catch (InterruptedException e) {}
		}
	}
}
