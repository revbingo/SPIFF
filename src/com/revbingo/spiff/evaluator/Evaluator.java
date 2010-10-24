/*******************************************************************************
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
 ******************************************************************************/
package com.revbingo.spiff.evaluator;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;

import java.util.HashMap;
import java.util.HashSet;

import com.revbingo.spiff.ExecutionException;

public class Evaluator {

	private Library lib;
	private EvaluatorMap variableMap;
	private HashSet<String> evaluatedExpressions;
	private Object[] context;
	private HashMap<String, CompiledExpression> cExpr = new HashMap<String, CompiledExpression>();

	public Evaluator() {

		Class<?>[] staticLib=new Class[1];
		Class<?>[] dynamicLib = new Class[1];
		Class<?>[] dotLib = new Class[0];

		staticLib[0] = java.lang.Math.class;
		dynamicLib[0] = EvaluatorMap.class;

		variableMap = new EvaluatorMap();
		evaluatedExpressions = new HashSet<String>();
		lib = new Library(staticLib, dynamicLib, dotLib, variableMap, null);
		context = new Object[1];
		context[0] = variableMap;
	}

	public void cacheExpression(String exp){
		if(exp.startsWith("&")) {
			exp = exp.substring(1);
		}
		evaluatedExpressions.add(exp);
	}

	public HashSet<String> getEvaluatedExpressions(){
		return evaluatedExpressions;
	}

	public boolean isReferenced(String name){
		return evaluatedExpressions.contains(name);
	}

	public void addVariable(String name, Object var){
		variableMap.addVariable(name, var);
	}

	private CompiledExpression compile(String expression) throws CompilationException {
		CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib);
		cExpr.put(expression, c);
		return c;
	}

	private CompiledExpression compile(String expression, Class<?> type) throws CompilationException {
		CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib, type);
		cExpr.put(expression + "@" + type.getSimpleName(), c);
		return c;
	}

	public int evaluateInt(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Integer.TYPE);
			return c.evaluate_int(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public long evaluateLong(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Long.TYPE);
			return c.evaluate_long(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public short evaluateShort(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Short.TYPE);
			return c.evaluate_short(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public boolean evaluateBoolean(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Boolean.TYPE);
			return c.evaluate_boolean(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public byte evaluateByte(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Byte.TYPE);
			return c.evaluate_byte(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public double evaluateDouble(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Double.TYPE);
			return c.evaluate_double(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public float evaluateFloat(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Float.TYPE);
			return c.evaluate_float(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public String evaluateString(String expression) throws ExecutionException{
		Object o;
		try {
			CompiledExpression c = getCompiledExpression(expression);
			o = c.evaluate(context);
			if(o instanceof String){
				return (String) o;
			} else {
				throw new ExecutionException("Expression resulted in non-String");
			}
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public Object evaluate(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression);
			return c.evaluate(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	CompiledExpression getCompiledExpression(String expression, Class<?> type) throws ExecutionException {
		CompiledExpression c = cExpr.get(expression + "@" + type.getSimpleName());
		if(c == null){
			try {
				c = compile(expression, type);
			} catch (CompilationException e) {
				throw new ExecutionException("Could not compile expression " + expression, e);
			}
		}
		return c;
	}

	CompiledExpression getCompiledExpression(String expression) throws ExecutionException {
		CompiledExpression c = cExpr.get(expression);

		if(c == null){
			try {
				c = compile(expression);
			} catch (CompilationException e) {
				throw new ExecutionException("Could not compile expression " + expression, e);
			}
		}
		return c;
	}

	public  void clear() {
		evaluatedExpressions.clear();
		variableMap.clear();
	}
}
