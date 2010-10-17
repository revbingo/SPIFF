package com.revbingo.spiff.events;

import com.revbingo.spiff.instructions.ReferencedInstruction;

public interface EventListener {

	public void notifyData(ReferencedInstruction ins);
	public void notifyGroup(String groupName, boolean start);
}