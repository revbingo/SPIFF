/*
 * Copyright Mark Piper 2016
 *
 * This file is part of SPIFF.
 *
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.revbingo.spiff.evaluator;

import com.revbingo.spiff.ExecutionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TestCaseEvaluator {

	private Evaluator unit;

	@Before
	public void setup() {
		unit = new EvalExEvaluator();
	}

	@Test
	public void evaluateIntegerExpr() throws Exception {
		unit.addVariable("a", 3);
		unit.addVariable("b", 5);

		assertThat(unit.evaluate("a + b", Integer.TYPE), equalTo(8));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateIntegerExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Integer.TYPE);
	}

//	@Test
//	@Ignore("Not supported by EvalEx")
//	public void evaluateShortExpr() throws Exception {
//		unit.addVariable("a", ((short) 1));
//		unit.addVariable("b", ((short) 3));
//
//		//Need to cast, evaluates to int by default
//		assertThat(unit.evaluate("(short) (a+b)", Short.TYPE), equalTo((short) 4));
//	}

	@Test(expected=ExecutionException.class)
	public void evaluateShortExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Short.TYPE);
	}

	@Test
	@Ignore("Not supported by EvalEx")
	public void evaluateByteExpr() throws Exception {
		unit.addVariable("a", (byte) 1);
		unit.addVariable("b", (byte) 4);

		assertThat(unit.evaluate("(byte) (a+b)", Byte.TYPE), equalTo((byte) 5));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateByteExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Byte.TYPE);
	}

	@Test
	public void evaluateFloatExpr() throws Exception {
		unit.addVariable("a", 2.12f);
		unit.addVariable("b", 3.5f);

		assertThat(unit.evaluate("a+b", Float.TYPE), equalTo(5.62f));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateFloatExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Float.TYPE);
	}

	@Test
	public void evaluateLongExpr() throws Exception {
		unit.addVariable("a", 2L);
		unit.addVariable("b", 3L);

		assertThat(unit.evaluate("a+b", Long.TYPE), equalTo(5L));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateLongExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Long.TYPE);
	}

	@Test
	public void evaluateDoubleExpr() throws Exception {
		unit.addVariable("a", 3.512d);
		unit.addVariable("b", 5.123d);

		assertThat(unit.evaluate("a+b", Double.TYPE), equalTo(8.635d));
	}

	@Test(expected=ExecutionException.class)
	public void evaluateDoubleExprWithDiv0() throws Exception {
		unit.evaluate("1/0", Double.TYPE);
	}

//	@Test
//	@Ignore("Not supported by EvalEx")
//	public void evaluateBooleanExpr() throws Exception {
//		unit.addVariable("a", true);
//		unit.addVariable("b", false);
//
////		assertThat(unit.evaluate("a || b", Boolean.TYPE), equalTo(true));
////		assertThat(unit.evaluate("a && b", Boolean.TYPE), equalTo(false));
//
//		unit.addVariable("c", true);
////		assertThat(unit.evaluate("a && (b || c)", Boolean.TYPE), equalTo(true));
//	}

	@Test(expected=ExecutionException.class)
	@Ignore("Not supported by EvalEx")
	public void evaluateBooleanExprWithDiv0() throws Exception {
//		unit.evaluate("1/0", Boolean.TYPE);
	}

	@Test
	public void evaluateDeterminesObject() throws Exception {
		assertThat(unit.evaluate("1 + 2"), instanceOf(Object.class));
	}

//	@Test
//	@Ignore("Not supported by EvalEx")
//	public void evaluateStringExpr() throws Exception {
//		unit.addVariable("a", "one");
//		unit.addVariable("b", "two");
//
////		assertThat(unit.evaluate("a + b", String.class), equalTo("onetwo"));
//	}

	@Test
	@Ignore("Not supported by EvalEx")
	public void evaluateHexLiteralAsInteger() throws Exception {
		assertThat(unit.evaluate("0x04 + 0xFF", Integer.TYPE), equalTo(259));
	}

//	@Test
//	@Ignore("Not supported by EvalEx")
//	public void expressionsAreCachedBasedOnString() throws ExecutionException {
//		CompiledExpression c = unit.getCompiledExpression("1 + 2");
//
//		assertThat(c, is(sameInstance(unit.getCompiledExpression("1 + 2"))));
//		assertThat(c, is(not(sameInstance(unit.getCompiledExpression("1+2")))));
//	}
//
//	@Test
//	@Ignore("Not supported by EvalEx")
//	public void typedExpressionsAreCachedBasedOnStringAndType() throws ExecutionException {
//		unit.addVariable("a", 2);
//		unit.addVariable("b", 3);
//
//		CompiledExpression longExpr = unit.getCompiledExpression("a + b", long.class);
//
//		assertThat(longExpr, is(sameInstance(unit.getCompiledExpression("a + b", long.class))));
//		assertThat(longExpr, is(not(sameInstance(unit.getCompiledExpression("a + b", int.class)))));
//	}

	@Test
	public void clearEvaluatorClearsExpressionsAndVariables() throws Exception {
		unit.addVariable("a", 2);
		int i = unit.evaluate("a + 3", Integer.TYPE);

		assertThat(i, is(5));

		unit.clear();

		try {
			unit.evaluate("a + 3", Integer.TYPE);
			fail("Should not have been able to evaluate variable a");
		} catch(ExecutionException ignored) {}
	}

//	@Test(expected=ExecutionException.class)
//	@Ignore("Not supported with EvalEx")
//	public void evaluateStringThrowsExceptionIfNotAString() throws ExecutionException {
////		unit.evaluate("1 + 2", String.class);
//	}

	@Test(expected=ExecutionException.class)
	public void divisionByZero() throws Exception {
		unit.evaluate("1/0");
	}

	@Test(expected=ExecutionException.class)
	public void unknownVarCausesException() throws Exception {
		unit.evaluate("unknown + missing");
	}

	@Test(expected=ExecutionException.class)
	public void badExpressionThrowsException() throws Exception {
		unit.evaluate("a---");
	}

//	@Test(expected=ExecutionException.class)
//	@Ignore("Not supported by EvalEx")
//	public void badTypedExpressionThrowsException() throws Exception {
////		unit.evaluate("a--", Boolean.TYPE);
//	}
}
