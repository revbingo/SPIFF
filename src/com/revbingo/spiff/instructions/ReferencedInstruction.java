package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;

public abstract class ReferencedInstruction extends Instruction {

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
	public void setAddress(int address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.addressStr = name + ".address";
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
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
