package com.revbingo.spiff.datatypes;

import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public abstract class NumberType extends Datatype {

	protected String literalExpr;
	
	public String getLiteral() {
		return this.literalExpr;
	}
	
	public void setLiteral(String literalExpr) {
		this.literalExpr = literalExpr;
	}
}
