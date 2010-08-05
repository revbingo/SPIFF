package com.revbingo.spiff.evaluator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class TestCaseEvaluator {

	@Test
	public void expressionCanBeAdded() throws Exception {
		Evaluator.addExpression("a+b");
		
		assertTrue(Evaluator.getEvaluatedExpressions().contains("a+b"));
	}
	
	@Test
	public void expressionIsReferenced() {
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
		Evaluator.addVariable("b", ((short) 1));

		//Need to cast, evaluates to int by default
		assertThat(Evaluator.evaluateShort("(short) (a+b)"), equalTo((short) 2));
	}
	
	@Test
	public void evaluateByteExpr() throws Exception {
		Evaluator.addVariable("a", (byte) 1);
		Evaluator.addVariable("b", (byte) 1);
		
		assertThat(Evaluator.evaluateByte("(byte) (a+b)"), equalTo((byte) 2));
	}
	
	
	@Test
	public void evaluateFloatExpr() throws Exception {
		Evaluator.addVariable("a", 2.0f);
		Evaluator.addVariable("b", 3.0f);
		
		assertThat(Evaluator.evaluateFloat("a+b"), equalTo(5.0f));
	}
	
	@Test
	public void evaluateLongExpr() throws Exception {
		Evaluator.addVariable("a", 2L);
		Evaluator.addVariable("b", 3L);
		
		assertThat(Evaluator.evaluateLong("a+b"), equalTo(5L));
	}
	
	@Test
	public void evaluateDoubleExpr() throws Exception {
		Evaluator.addVariable("a", 3.0d);
		Evaluator.addVariable("b", 5.0d);
		
		assertThat(Evaluator.evaluateDouble("a+b"), equalTo(8.0d));
	}

	@Test
	public void evaluateBooleanExpr() throws Exception {
		Evaluator.addVariable("a", true);
		Evaluator.addVariable("b", false);
		
		assertThat(Evaluator.evaluateBoolean("a || b"), equalTo(true));
		assertThat(Evaluator.evaluateBoolean("a && b"), equalTo(false));
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
