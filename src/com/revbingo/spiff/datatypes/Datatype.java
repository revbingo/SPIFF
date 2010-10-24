/*******************************************************************************
 * This file is part of SPIFF.
 *
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package com.revbingo.spiff.datatypes;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.Instruction;

public abstract class Datatype implements Instruction {

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
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		address = buffer.position();
		this.value = this.evaluate(buffer);
		if(isReferenced){
			Evaluator.getInstance().addVariable(name, value);
			Evaluator.getInstance().addVariable(addressStr, this.address);
		}
		eventDispatcher.notifyData(this);
	}

	public abstract Object evaluate(ByteBuffer buffer) throws ExecutionException;
}
