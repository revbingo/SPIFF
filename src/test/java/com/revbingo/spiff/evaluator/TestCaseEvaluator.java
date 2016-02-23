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

import com.sun.org.apache.xpath.internal.operations.Bool;
import gnu.jel.CompiledExpression;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

public class TestCaseEvaluator {

	@Test
	public void evaluateIntegerExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 3);
		unit.addVariable("b", 5);

		assertThat(unit.evaluate("a + b", Integer.TYPE), equalTo(8));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateIntegerExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Integer.TYPE);
	}

	@Test
	public void evaluateShortExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", ((short) 1));
		unit.addVariable("b", ((short) 3));

		//Need to cast, evaluates to int by default
		assertThat(unit.evaluate("(short) (a+b)", Short.TYPE), equalTo((short) 4));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateShortExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Short.TYPE);
	}

	@Test
	public void evaluateByteExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", (byte) 1);
		unit.addVariable("b", (byte) 4);

		assertThat(unit.evaluate("(byte) (a+b)", Byte.TYPE), equalTo((byte) 5));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateByteExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Byte.TYPE);
	}

	@Test
	public void evaluateFloatExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2.12f);
		unit.addVariable("b", 3.5f);

		assertThat(unit.evaluate("a+b", Float.TYPE), equalTo(5.62f));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateFloatExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Float.TYPE);
	}

	@Test
	public void evaluateLongExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 2L);
		unit.addVariable("b", 3L);

		assertThat(unit.evaluate("a+b", Long.TYPE), equalTo(5L));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateLongExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Long.TYPE);
	}

	@Test
	public void evaluateDoubleExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", 3.512d);
		unit.addVariable("b", 5.123d);

		assertThat(unit.evaluate("a+b", Double.TYPE), equalTo(8.635d));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateDoubleExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Double.TYPE);
	}

	@Test
	public void evaluateBooleanExpr() throws Exception {
		Evaluator unit = new Evaluator();
		unit.addVariable("a", true);
		unit.addVariable("b", false);

		assertThat(unit.evaluate("a || b", Boolean.TYPE), equalTo(true));
		assertThat(unit.evaluate("a && b", Boolean.TYPE), equalTo(false));

		unit.addVariable("c", true);
		assertThat(unit.evaluate("a && (b || c)", Boolean.TYPE), equalTo(true));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateBooleanExprWithDiv0() throws Exception {
		Evaluator unit = new Evaluator();
		unit.evaluate("1/0", Boolean.TYPE);
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

		assertThat(unit.evaluate("a + b", String.class), equalTo("onetwo"));
	}

	@Test
	public void evaluateHexLiteralAsInteger() throws Exception {
		Evaluator unit = new Evaluator();
		assertThat(unit.evaluate("0x04 + 0xFF", Integer.TYPE), equalTo(259));
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
		int i = unit.evaluate("a + 3", Integer.TYPE);

		assertThat(i, is(5));

		unit.clear();

		try {
			unit.evaluate("a + 3", Integer.TYPE);
			fail("Should not have been able to evaluate variable a");
		} catch(ExecutionException e) {}
	}

	@Test(expected=ExecutionException.class)
	public void evaluateStringThrowsExceptionIfNotAString() throws ExecutionException {
		Evaluator unit = new Evaluator();
		unit.evaluate("1 + 2", String.class);
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
		unit.evaluate("a--", Boolean.TYPE);
	}

}
