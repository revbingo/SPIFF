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

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;

import java.util.HashMap;

import com.revbingo.spiff.ExecutionException;

public class Evaluator {

	private Library lib;
	private EvaluatorMap variableMap;
	private Object[] context;
	private HashMap<String, CompiledExpression> cExpr = new HashMap<String, CompiledExpression>();

	public Evaluator() {

		Class<?>[] staticLib=new Class[] { java.lang.Math.class };
		Class<?>[] dynamicLib = new Class[] { EvaluatorMap.class };
		Class<?>[] dotLib = new Class[] {};

		variableMap = new EvaluatorMap();
		lib = new Library(staticLib, dynamicLib, dotLib, variableMap, null);
		context = new Object[] { variableMap };
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

	public Object evaluate(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression);
			return c.evaluate(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	@SuppressWarnings("unchecked")
	public <X, T extends Class<X>> X evaluate(String expression, T type) {
		try {
			CompiledExpression c = getCompiledExpression(expression, type);
			return (X) c.evaluate(context);
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

	public void clear() {
		variableMap.clear();
	}
}
