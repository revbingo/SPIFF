package com.revbingo.spiff.evaluator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class TestCaseEvaluator {

	@Test
	public void expressionCanBeAddedToListOfEvaluatedExpressions() throws Exception {
		Evaluator.addExpression("a+b");
		
		assertTrue(Evaluator.getEvaluatedExpressions().contains("a+b"));
	}
	
	@Test
	public void expressionIsMarkedAsReferenced() {
		Evaluator.addExpression("a+b");
		assertTrue(Evaluator.isReferenced("a+b"));
	}
	
	@Test
	public void evaluateIntegerExpr() throws Exception {
		Evaluator.addVariable("a", 3);
		Evaluator.addVariable("b", 5);
		
		assertThat(Evaluator.evaluateInt("a+b"), equalTo(8));
	}
	
	@Test
	public void evaluateShortExpr() throws Exception {
		Evaluator.addVariable("a", ((short) 1));
		Evaluator.addVariable("b", ((short) 3));

		//Need to cast, evaluates to int by default
		assertThat(Evaluator.evaluateShort("(short) (a+b)"), equalTo((short) 4));
	}
	
	@Test
	public void evaluateByteExpr() throws Exception {
		Evaluator.addVariable("a", (byte) 1);
		Evaluator.addVariable("b", (byte) 4);
		
		assertThat(Evaluator.evaluateByte("(byte) (a+b)"), equalTo((byte) 5));
	}
	
	
	@Test
	public void evaluateFloatExpr() throws Exception {
		Evaluator.addVariable("a", 2.12f);
		Evaluator.addVariable("b", 3.5f);
		
		assertThat(Evaluator.evaluateFloat("a+b"), equalTo(5.62f));
	}
	
	@Test
	public void evaluateLongExpr() throws Exception {
		Evaluator.addVariable("a", 2L);
		Evaluator.addVariable("b", 3L);
		
		assertThat(Evaluator.evaluateLong("a+b"), equalTo(5L));
	}
	
	@Test
	public void evaluateDoubleExpr() throws Exception {
		Evaluator.addVariable("a", 3.512d);
		Evaluator.addVariable("b", 5.123d);
		
		assertThat(Evaluator.evaluateDouble("a+b"), equalTo(8.635d));
	}

	@Test
	public void evaluateBooleanExpr() throws Exception {
		Evaluator.addVariable("a", true);
		Evaluator.addVariable("b", false);
		
		assertThat(Evaluator.evaluateBoolean("a || b"), equalTo(true));
		assertThat(Evaluator.evaluateBoolean("a && b"), equalTo(false));
		
		Evaluator.addVariable("c", true);
		assertThat(Evaluator.evaluateBoolean("a && (b || c)"), equalTo(true));
	}
	
	@Test
	public void evaluateStringExpr() throws Exception {
		Evaluator.addVariable("a", "one");
		Evaluator.addVariable("b", "two");
		
		assertThat(Evaluator.evaluateString("a + b"), equalTo("onetwo"));
	}
	
	@Test(expected=ExecutionException.class)
	public void unknownVarCausesException() throws Exception {
		Evaluator.evaluate("unknown + missing");
	}
	
	@Test(expected=ExecutionException.class)
	public void badExpressionThrowsException() throws Exception {
		Evaluator.evaluate("a---");
	}
}
