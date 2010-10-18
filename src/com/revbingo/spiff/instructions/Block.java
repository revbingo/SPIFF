/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventListener;

public class Block implements Instruction, Iterable<Instruction> {

	protected List<Instruction> instructions;
	
	public Block(){}
	
	public Block(List<Instruction> i){
		instructions = i;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		for(Instruction inst : instructions) {
			inst.execute(buffer, eventDispatcher);
		}
	}
	
	public void setInstructions(List<Instruction> ins){
		instructions = ins;
	}

	public List<Instruction> getInstructions(){
		return instructions;
	}

	@Override
	public Iterator<Instruction> iterator() {
		return instructions.iterator();
	}	
}
