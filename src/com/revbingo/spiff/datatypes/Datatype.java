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
package com.revbingo.spiff.datatypes;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.AdfInstruction;

public abstract class Datatype extends AdfInstruction {

	public int address;  //address this instruction was executed
	public String name;  //name given in map
	private String addressStr;
	public Object value; //final value
	public boolean isReferenced = true; //assume it's referenced by default

	public void setReferenced(boolean b){
		isReferenced = b;
	}

	public int getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		this.addressStr = name + ".address";
	}

	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher, Evaluator evaluator) throws ExecutionException {
		address = buffer.position();
		this.value = this.evaluate(buffer, evaluator);
		if(isReferenced){
			evaluator.addVariable(name, value);
			evaluator.addVariable(addressStr, this.address);
		}
		eventDispatcher.notifyData(this);
	}

	public abstract Object evaluate(ByteBuffer buffer, Evaluator evaluator) throws ExecutionException;
}
