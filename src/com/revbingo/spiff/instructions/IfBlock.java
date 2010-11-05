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
import java.util.ArrayList;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public class IfBlock extends Block {

	private String expression;
	private Block elseInsts;

	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher, Evaluator evaluator) throws ExecutionException {
		boolean result = evaluator.evaluateBoolean(expression);
		if(result){
			super.execute(buffer, eventDispatcher, evaluator);
		}else{
			if(elseInsts != null){
				for(Instruction ins: elseInsts){
					ins.execute(buffer, eventDispatcher, evaluator);
				}
			}
		}
	}

	public void setIfExpression(String expr) {
		expression = expr;
	}

	public String getIfExpression() {
		return expression;
	}

	public void setElseInstructions(List<Instruction> inst){
		elseInsts = new Block(inst);
	}

	public Block getElseInstructions() {
		return elseInsts;
	}

	public Block getIfInstructions() {
		return new Block(instructions);
	}

	@Override
	public ArrayList<Instruction> getInstructions(){
		ArrayList<Instruction> a = new ArrayList<Instruction>(instructions);
		if(elseInsts != null) a.addAll(elseInsts.getInstructions());
		return a;
	}
}
