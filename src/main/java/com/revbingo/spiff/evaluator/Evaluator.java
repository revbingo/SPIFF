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

		Class<?>[] staticLib=new Class[1];
		Class<?>[] dynamicLib = new Class[1];
		Class<?>[] dotLib = new Class[0];

		staticLib[0] = java.lang.Math.class;
		dynamicLib[0] = EvaluatorMap.class;

		variableMap = new EvaluatorMap();
		lib = new Library(staticLib, dynamicLib, dotLib, variableMap, null);
		context = new Object[1];
		context[0] = variableMap;
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
