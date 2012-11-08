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
		elseInsts = new Block();
		elseInsts.setInstructions(inst);
	}

	public Block getElseInstructions() {
		return elseInsts;
	}

	public Block getIfInstructions() {
		Block ifInstructions = new Block();
		ifInstructions.setInstructions(instructions);
		return ifInstructions;
	}

	@Override
	public ArrayList<Instruction> getInstructions(){
		ArrayList<Instruction> a = new ArrayList<Instruction>(instructions);
		if(elseInsts != null) a.addAll(elseInsts.getInstructions());
		return a;
	}
}
