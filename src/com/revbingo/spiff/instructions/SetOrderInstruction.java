package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventListener;

public class SetOrderInstruction implements Instruction {

	private ByteOrder order;
		
	public SetOrderInstruction(){}
	
	public void setOrder(ByteOrder order){
		this.order = order; 
	}
	
	public ByteOrder getOrder() {
		return order;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		buffer.order(order);
	}

}
