package com.revbingo.spiff.events;

import com.revbingo.spiff.instructions.ReferencedInstruction;

public class DebugEventListener implements EventListener {

	int tabCount = 0;

	@Override
	public void notifyData(ReferencedInstruction ins) {
		for(int i=0;i<tabCount;i++) {
			System.out.print("\t");
		}
		System.out.println(ins.name + ":" + ins.value);
	}

	@Override
	public void notifyGroup(String groupName, boolean start) {
		if(!start) {
			tabCount--;
		}

		for(int i=0;i<tabCount;i++) {
			System.out.print("\t");
		}
		System.out.println((start ? "start " : "end ") + groupName);

		if(start) {
			tabCount++;
		}
	}

}
