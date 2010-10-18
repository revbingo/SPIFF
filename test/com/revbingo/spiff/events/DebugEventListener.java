/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.events;

import com.revbingo.spiff.instructions.ReferencedInstruction;

public class DebugEventListener implements EventListener {

	int tabCount = 0;

	@Override
	public void notifyData(ReferencedInstruction ins) {
		for(int i=0;i<tabCount;i++) {
			System.out.print("\t");
		}
		System.out.println("[" + ins.name + "] " + ins.value);
	}

	@Override
	public void notifyGroup(String groupName, boolean start) {
		if(!start) {
			tabCount--;
		}

		for(int i=0;i<tabCount;i++) {
			System.out.print("\t");
		}
		System.out.println((start ? ">> " : "<< ") + "[" + groupName + "]");

		if(start) {
			tabCount++;
		}
	}

}
