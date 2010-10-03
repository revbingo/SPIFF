package com.revbingo.spiff.evaluator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gnu.jel.CompiledExpression;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

public class TestCaseEvaluator {

	@Test
	public void expressionCanBeAddedToListOfEvaluatedExpressions() throws Exception {
		Evaluator.cacheExpression("a+b");
		
		assertTrue(Evaluator.getEvaluatedExpressions().contains("a+b"));
	}
	
	@Test
	public void expressionIsMarkedAsReferenced() {
		Evaluator.cacheExpression("a+b");
		assertTrue(Evaluator.isReferenced("a+b"));
	}
	
	@Test
	public void evaluateIntegerExpr() throws Exception {
		Evaluator.addVariable("a", 3);
		Evaluator.addVariable("b", 5);
		
		assertThat(Evaluator.evaluateInt("a + b"), equalTo(8));
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateIntegerExprWithDiv0() throws Exception {
		Evaluator.evaluateInt("1/0");
	}
	
	@Test
	public void evaluateShortExpr() throws Exception {
		Evaluator.addVariable("a", ((short) 1));
		Evaluator.addVariable("b", ((short) 3));

		//Need to cast, evaluates to int by default
		assertThat(Evaluator.evaluateShort("(short) (a+b)"), equalTo((short) 4));
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateShortExprWithDiv0() throws Exception {
		Evaluator.evaluateShort("1/0");
	}
	
	@Test
	public void evaluateByteExpr() throws Exception {
		Evaluator.addVariable("a", (byte) 1);
		Evaluator.addVariable("b", (byte) 4);
		
		assertThat(Evaluator.evaluateByte("(byte) (a+b)"), equalTo((byte) 5));
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateByteExprWithDiv0() throws Exception {
		Evaluator.evaluateByte("1/0");
	}
	
	@Test
	public void evaluateFloatExpr() throws Exception {
		Evaluator.addVariable("a", 2.12f);
		Evaluator.addVariable("b", 3.5f);
		
		assertThat(Evaluator.evaluateFloat("a+b"), equalTo(5.62f));
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateFloatExprWithDiv0() throws Exception {
		Evaluator.evaluateFloat("1/0");
	}
	
	@Test
	public void evaluateLongExpr() throws Exception {
		Evaluator.addVariable("a", 2L);
		Evaluator.addVariable("b", 3L);
		
		assertThat(Evaluator.evaluateLong("a+b"), equalTo(5L));
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateLongExprWithDiv0() throws Exception {
		Evaluator.evaluateLong("1/0");
	}
	
	@Test
	public void evaluateDoubleExpr() throws Exception {
		Evaluator.addVariable("a", 3.512d);
		Evaluator.addVariable("b", 5.123d);
		
		assertThat(Evaluator.evaluateDouble("a+b"), equalTo(8.635d));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateDoubleExprWithDiv0() throws Exception {
		Evaluator.evaluateDouble("1/0");
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
	
	@Test(expected=ExecutionException.class)
	public void evaluateBooleanExprWithDiv0() throws Exception {
		Evaluator.evaluateBoolean("1/0");
	}
	
	@Test 
	public void evaluateDeterminesObject() throws Exception {
		assertThat(Evaluator.evaluate("1 + 2"), instanceOf(Object.class));
	}
	
	@Test
	public void evaluateStringExpr() throws Exception {
		Evaluator.addVariable("a", "one");
		Evaluator.addVariable("b", "two");
		
		assertThat(Evaluator.evaluateString("a + b"), equalTo("onetwo"));
	}
	
	@Test
	public void expressionsAreCachedBasedOnString() throws ExecutionException {
		CompiledExpression c = Evaluator.getCompiledExpression("1 + 2");
		
		assertThat(c, is(sameInstance(Evaluator.getCompiledExpression("1 + 2"))));
		assertThat(c, is(not(sameInstance(Evaluator.getCompiledExpression("1+2")))));
	}
	
	@Test
	public void typedExpressionsAreCachedBasedOnStringAndType() throws ExecutionException {
		Evaluator.addVariable("a", 2);
		Evaluator.addVariable("b", 3);
		
		CompiledExpression shortExpr = Evaluator.getCompiledExpression("a + b", long.class);
		
		assertThat(shortExpr, is(sameInstance(Evaluator.getCompiledExpression("a + b", long.class))));
		assertThat(shortExpr, is(not(sameInstance(Evaluator.getCompiledExpression("a + b", int.class)))));
	}
	
	@Test
	public void clearEvaluatorClearsExpressionsAndVariables() throws Exception {
		Evaluator.addVariable("a", 2);
		int i = Evaluator.evaluateInt("a + 3");
		
		assertThat(i, is(5));
		
		Evaluator.clear();
		
		try {
			Evaluator.evaluateInt("a + 3");
			fail("Should not have been able to evaluate variable a");
		} catch(ExecutionException e) {}
	}
	
	@Test(expected=ExecutionException.class)
	public void evaluateStringThrowsExceptionIfNotAString() throws ExecutionException {
		Evaluator.evaluateString("1 + 2");
	}

	@Test(expected=ExecutionException.class)
	public void divisionByZero() throws Exception {
		Evaluator.evaluate("1/0");
	}
	
	@Test(expected=ExecutionException.class)
	public void unknownVarCausesException() throws Exception {
		Evaluator.evaluate("unknown + missing");
	}
	
	@Test(expected=ExecutionException.class)
	public void badExpressionThrowsException() throws Exception {
		Evaluator.evaluate("a---");
	}

	@Test(expected=ExecutionException.class)
	public void badTypedExpressionThrowsException() throws Exception {
		Evaluator.evaluateBoolean("a---");
	}
	
}
