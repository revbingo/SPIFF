package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventDispatcher;

public class Block implements Instruction, Iterable<Instruction> {

	protected List<Instruction> instructions;
	
	public Block(){}
	
	public Block(List<Instruction> i){
		instructions = i;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
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
