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
	public void evaluateIntegerExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 3);
		unit.addVariable("b", 5);

		assertThat(unit.evaluateInt("a + b"), equalTo(8));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateIntegerExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateInt("1/0");
	}

	@Test
	public void evaluateShortExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", ((short) 1));
		unit.addVariable("b", ((short) 3));

		//Need to cast, evaluates to int by default
		assertThat(unit.evaluateShort("(short) (a+b)"), equalTo((short) 4));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateShortExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateShort("1/0");
	}

	@Test
	public void evaluateByteExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", (byte) 1);
		unit.addVariable("b", (byte) 4);

		assertThat(unit.evaluateByte("(byte) (a+b)"), equalTo((byte) 5));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateByteExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateByte("1/0");
	}

	@Test
	public void evaluateFloatExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2.12f);
		unit.addVariable("b", 3.5f);

		assertThat(unit.evaluateFloat("a+b"), equalTo(5.62f));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateFloatExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateFloat("1/0");
	}

	@Test
	public void evaluateLongExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2L);
		unit.addVariable("b", 3L);

		assertThat(unit.evaluateLong("a+b"), equalTo(5L));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateLongExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateLong("1/0");
	}

	@Test
	public void evaluateDoubleExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 3.512d);
		unit.addVariable("b", 5.123d);

		assertThat(unit.evaluateDouble("a+b"), equalTo(8.635d));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateDoubleExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateDouble("1/0");
	}

	@Test
	public void evaluateBooleanExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", true);
		unit.addVariable("b", false);

		assertThat(unit.evaluateBoolean("a || b"), equalTo(true));
		assertThat(unit.evaluateBoolean("a && b"), equalTo(false));

		unit.addVariable("c", true);
		assertThat(unit.evaluateBoolean("a && (b || c)"), equalTo(true));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateBooleanExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateBoolean("1/0");
	}

	@Test
	public void evaluateDeterminesObject() throws Exception {
		Evaluator unit = new Evaluator();
		assertThat(unit.evaluate("1 + 2"), instanceOf(Object.class));
	}

	@Test
	public void evaluateStringExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", "one");
		unit.addVariable("b", "two");

		assertThat(unit.evaluateString("a + b"), equalTo("onetwo"));
	}

	@Test
	public void evaluateHexLiteralAsInteger() throws Exception {
		Evaluator unit = new Evaluator();
		assertThat(unit.evaluateInt("0x04 + 0xFF"), equalTo(259));
	}

	@Test
	public void expressionsAreCachedBasedOnString() throws ExecutionException {
		Evaluator unit = new Evaluator();
		CompiledExpression c = unit.getCompiledExpression("1 + 2");

		assertThat(c, is(sameInstance(unit.getCompiledExpression("1 + 2"))));
		assertThat(c, is(not(sameInstance(unit.getCompiledExpression("1+2")))));
	}

	@Test
	public void typedExpressionsAreCachedBasedOnStringAndType() throws ExecutionException {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2);
		unit.addVariable("b", 3);

		CompiledExpression shortExpr = unit.getCompiledExpression("a + b", long.class);

		assertThat(shortExpr, is(sameInstance(unit.getCompiledExpression("a + b", long.class))));
		assertThat(shortExpr, is(not(sameInstance(unit.getCompiledExpression("a + b", int.class)))));
	}

	@Test
	public void clearEvaluatorClearsExpressionsAndVariables() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2);
		int i = unit.evaluateInt("a + 3");

		assertThat(i, is(5));

		unit.clear();

		try {
			unit.evaluateInt("a + 3");
			fail("Should not have been able to evaluate variable a");
		} catch(ExecutionException e) {}
	}

	@Test(expected=ExecutionException.class)
	public void evaluateStringThrowsExceptionIfNotAString() throws ExecutionException {
		Evaluator unit = new Evaluator();
		unit.evaluateString("1 + 2");
	}

	@Test(expected=ExecutionException.class)
	public void divisionByZero() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0");
	}

	@Test(expected=ExecutionException.class)
	public void unknownVarCausesException() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("unknown + missing");
	}

	@Test(expected=ExecutionException.class)
	public void badExpressionThrowsException() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("a---");
	}

	@Test(expected=ExecutionException.class)
	public void badTypedExpressionThrowsException() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluateBoolean("a---");
	}

}