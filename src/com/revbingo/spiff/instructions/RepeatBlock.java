package com.revbingo.spiff.instructions;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;
import com.revbingo.spiff.events.EventDispatcher;
import com.revbingo.spiff.parser.ParseException;

public class RepeatBlock extends Block {

	private String repeatCountExpr;
	
	public void setRepeatCountExpression(String expr) throws ParseException {
		repeatCountExpr=expr;
	}
	
	@Override
	public void execute(ByteBuffer buffer, EventDispatcher eventDispatcher) throws ExecutionException {
		long repeatCount = Evaluator.evaluateLong(repeatCountExpr);
		for(int x=0;x<repeatCount;x++){
			super.execute(buffer, eventDispatcher);
		}
	}
	
}
