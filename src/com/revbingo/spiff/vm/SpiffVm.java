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
	private boolean isSuspended;
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
