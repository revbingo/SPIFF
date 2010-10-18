/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.evaluator;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;

import java.util.HashMap;
import java.util.HashSet;

import com.revbingo.spiff.ExecutionException;

public class Evaluator {

	private static Library lib;
	private static EvaluatorMap variableMap;
	private static HashSet<String> evaluatedExpressions;
	private static Object[] context;
	private static HashMap<String, CompiledExpression> cExpr = new HashMap<String, CompiledExpression>();

	static {
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

	public static void cacheExpression(String exp){
		if(exp.startsWith("&")) {
			exp = exp.substring(1);
		}
		evaluatedExpressions.add(exp);
	}

	public static HashSet<String> getEvaluatedExpressions(){
		return evaluatedExpressions;
	}

	public static boolean isReferenced(String name){
		return evaluatedExpressions.contains(name);
	}

	public static void addVariable(String name, Object var){
		variableMap.addVariable(name, var);
	}

	private static CompiledExpression compile(String expression) throws CompilationException {
		CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib);
		cExpr.put(expression, c);
		return c;
	}

	private static CompiledExpression compile(String expression, Class<?> type) throws CompilationException {
		CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib, type);
		cExpr.put(expression + "@" + type.getSimpleName(), c);
		return c;
	}

	public static int evaluateInt(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Integer.TYPE);
			return c.evaluate_int(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static long evaluateLong(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Long.TYPE);
			return c.evaluate_long(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static short evaluateShort(String expression) throws ExecutionException {
		try {
			CompiledExpression c = getCompiledExpression(expression, Short.TYPE);
			return c.evaluate_short(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static boolean evaluateBoolean(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Boolean.TYPE);
			return c.evaluate_boolean(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static byte evaluateByte(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Byte.TYPE);
			return c.evaluate_byte(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static double evaluateDouble(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Double.TYPE);
			return c.evaluate_double(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static float evaluateFloat(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression, Float.TYPE);
			return c.evaluate_float(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	public static String evaluateString(String expression) throws ExecutionException{
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

	public static Object evaluate(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression);
			return c.evaluate(context);
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}
	}

	static CompiledExpression getCompiledExpression(String expression, Class<?> type) throws ExecutionException {
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

	static CompiledExpression getCompiledExpression(String expression) throws ExecutionException {
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

	public static void clear() {
		evaluatedExpressions.clear();
		variableMap.clear();
	}
}
