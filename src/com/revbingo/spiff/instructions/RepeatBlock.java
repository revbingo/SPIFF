/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.parser.ParseException;

public class RepeatBlock extends Block {

	private String repeatCountExpr;
	
	public void setRepeatCountExpression(String expr) throws ParseException {
		repeatCountExpr=expr;
	}
	
	public String getRepeatCountExpression() {
		return repeatCountExpr;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		long repeatCount = (long) Evaluator.evaluateDouble(repeatCountExpr);
		for(int x=0;x<repeatCount;x++){
			super.execute(buffer, eventDispatcher);
		}
	}
	
}
