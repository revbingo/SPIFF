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

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventListener;

public class SkipInstruction extends AdfInstruction {

	private String skipExpr;

	@Override
	public void execute(ByteBuffer buffer, EventListener eventDispatcher, Evaluator evaluator) throws ExecutionException {
		int length = evaluator.evaluateInt(skipExpr);
		buffer.position(buffer.position() + length);
	}

	public void setExpression(String ex) {
		skipExpr = ex;
	}

	public String getExpression() {
		return skipExpr;
	}

}