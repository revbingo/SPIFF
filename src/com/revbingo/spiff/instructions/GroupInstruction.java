package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.EventDispatcher;

public class GroupInstruction extends Instruction {

	private String groupName;
	
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		eventDispatcher.notifyGroup(groupName, true);
	}
	
	public void setGroupName(String name){
		groupName = name;
	}

}
