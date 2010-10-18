/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.parser.ParseException;

public class IfBlock extends Block {

	private String expression;
	private Block elseInsts;
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		boolean result = Evaluator.evaluateBoolean(expression);
		if(result){
			super.execute(buffer, eventDispatcher);
		}else{
			if(elseInsts != null){
				for(Instruction ins: elseInsts){
					ins.execute(buffer, eventDispatcher);
				}
			}
		}
	}
	
	public void setIfExpression(String expr) throws ParseException{
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
	
	public ArrayList<Instruction> getInstructions(){
		ArrayList<Instruction> a = new ArrayList<Instruction>(instructions);
		if(elseInsts != null) a.addAll(elseInsts.getInstructions());
		return a;
	}
}
