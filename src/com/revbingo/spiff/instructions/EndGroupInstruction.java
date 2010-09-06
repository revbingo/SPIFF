package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventDispatcher;

public class EndGroupInstruction implements Instruction {

	private String groupName;
	
	public EndGroupInstruction() { super(); }

	public EndGroupInstruction(String name) {
		this.setGroupName(name);
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		eventDispatcher.notifyGroup(groupName, false);
	}
	
	public void setGroupName(String name){
		groupName = name;
	}
	
	public String getGroupName() {
		return groupName;
	}

}
