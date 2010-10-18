/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public abstract class ReferencedInstruction implements Instruction {

	public int address;  //address this instruction was executed
	public String name;  //name given in map
	private String addressStr;
	public Object value; //final value
	public boolean isReferenced = true; //assume it's referenced by default
	
	public void setReferenced(boolean b){
		isReferenced = b;
	}
	
	public int getAddress() {
		return address;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.addressStr = name + ".address";
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		address = buffer.position();
		this.value = this.evaluate(buffer);
		if(isReferenced){
			Evaluator.addVariable(name, value);
			Evaluator.addVariable(addressStr, this.address);
		}
		eventDispatcher.notifyData(this);
	}
	
	public abstract Object evaluate(ByteBuffer buffer) throws ExecutionException;
}
