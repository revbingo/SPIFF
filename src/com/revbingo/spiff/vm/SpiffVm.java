package com.revbingo.spiff.vm;

import java.nio.ByteBuffer;
import java.util.List;

import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;
import com.revbingo.spiff.instructions.AdfInstruction;

public class SpiffVm {

	private List<Instruction> instructions;
	private ByteBuffer buffer;
	private EventListener ed;
	private Evaluator evaluator = new Evaluator();
	private int programCounter;
	private boolean isStepping;
	private boolean isSuspended;
	private boolean isCompleted;
	private Object suspendedLock = new Object();

	public SpiffVm(List<Instruction> instructions, ByteBuffer buffer,
			EventListener ed) {
		this.instructions = instructions;
		this.buffer = buffer;
		this.ed = ed;

		evaluator.addVariable("fileLength", buffer.limit());
	}

	public void start() {
		for(Instruction i : instructions) {
			if(i instanceof AdfInstruction) {
				AdfInstruction vmi = (AdfInstruction) i;
				programCounter =vmi.lineNumber;

				if(vmi.isBreakpoint) {
					isSuspended = true;
					synchronized (suspendedLock) {
						try {
							suspendedLock.wait();
						} catch (InterruptedException e) {}
					}
				}
			}

			i.execute(buffer, ed, evaluator);
		}
		isCompleted = true;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public boolean isComplete() {
		return isCompleted;
	}

	public Object getVar(String expression) {
		return evaluator.evaluate(expression);
	}

	public Integer getNextLineNumber() {
		return programCounter;
	}

	public void setBreakpoint(int i) {
		for(Instruction inst : instructions) {
			if(((AdfInstruction) inst).lineNumber == i) {
				((AdfInstruction) inst).isBreakpoint = true;
			}
		}
	}

	public void resume() {
		synchronized (suspendedLock) {
			suspendedLock.notify();
		}
	}
}
