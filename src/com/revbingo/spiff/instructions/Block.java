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
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public class Block implements Instruction, Iterable<Instruction> {

	protected List<Instruction> instructions;

	public Block(){}

	public Block(List<Instruction> i){
		instructions = i;
	}

	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher, Evaluator evaluator) throws ExecutionException {
		for(Instruction inst : instructions) {
			inst.execute(buffer, eventDispatcher, evaluator);
		}
	}

	public void setInstructions(List<Instruction> ins){
		instructions = ins;
	}

	public List<Instruction> getInstructions(){
		return instructions;
	}

	@Override
	public Iterator<Instruction> iterator() {
		return instructions.iterator();
	}
}
