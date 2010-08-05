package com.revbingo.spiff.events;

import com.revbingo.spiff.instructions.ReferencedInstruction;

public interface ContentHandler {

	public void notifyStartGroup(String groupName);
	public void notifyEndGroup(String groupName);
	public void notifyData(ReferencedInstruction ins);
	public void notifyStart();
	public void notifyEnd();
}
