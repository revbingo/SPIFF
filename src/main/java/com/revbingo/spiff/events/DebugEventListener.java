/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.events;

import com.revbingo.spiff.datatypes.Datatype;

public class DebugEventListener implements EventListener {

	int tabCount = 0;

	private EventListener wrappedListener;
	
	public DebugEventListener() {
		this(new EventListener() {

			@Override
			public void notifyData(Datatype ins) {
			}
			
			@Override
			public void notifyGroup(String groupName, boolean start) {
			}
		});
	}
	
	public DebugEventListener(EventListener wrappedListener) {
		this.wrappedListener = wrappedListener;
	}
	
	@Override
	public void notifyData(Datatype ins) {
		for(int i=0;i<tabCount;i++) {
			System.out.print("\t");
		}
		System.out.println("[" + ins.getName() + "] " + ins.getValue());
		wrappedListener.notifyData(ins);
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
		
		wrappedListener.notifyGroup(groupName, start);
	}

}
