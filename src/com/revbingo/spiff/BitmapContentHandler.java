/**
 * 
 */
package com.revbingo.spiff;

import com.revbingo.spiff.events.ContentHandler;
import com.revbingo.spiff.instructions.ReferencedInstruction;

final class BitmapContentHandler implements ContentHandler {
	@Override
	public void notifyData(ReferencedInstruction ins) {
		System.out.println(ins.name + " " + ins.value);
	}

	@Override
	public void notifyEnd() {
		System.out.println("the end");
	}

	@Override
	public void notifyStartGroup(String groupName) {
		System.out.println("**** start group " + groupName);
	}
	
	@Override
	public void notifyStart() {
		System.out.println("the start");
	}

	@Override
	public void notifyEndGroup(String groupName) {
		System.out.println("******** end group " + groupName);
	}
}