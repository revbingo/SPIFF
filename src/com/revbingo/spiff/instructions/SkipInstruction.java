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

public class SkipInstruction implements Instruction {

	private String skipExpr;
	
	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher) throws ExecutionException {
		int length = Evaluator.evaluateInt(skipExpr);
		buffer.position(buffer.position() + length);
	}
	
	public void setSizeExpression(String ex) throws ParseException {
		skipExpr = ex;
	}

}
