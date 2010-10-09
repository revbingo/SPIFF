package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventListener;

public class GroupInstruction implements Instruction {

	private String groupName;
	
	public GroupInstruction() { super(); }
	
	public GroupInstruction(String name) {
		this.setGroupName(name);
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		eventDispatcher.notifyGroup(groupName, true);
	}
	
	public void setGroupName(String name){
		groupName = name;
	}

	public String getGroupName() {
		return groupName;
	}

}
