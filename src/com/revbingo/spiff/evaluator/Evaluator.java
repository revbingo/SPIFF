package com.revbingo.spiff.evaluator;

import gnu.jel.CompilationException;
import gnu.jel.CompiledExpression;
import gnu.jel.Library;

import java.util.HashMap;
import java.util.HashSet;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.parser.ParseException;

public class Evaluator {

	private static Library lib;
	private static EvaluatorMap map;
	private static HashSet<String> evaluatedExpressions;
	private static Object[] context;
	private static HashMap<String, CompiledExpression> cExpr = new HashMap<String, CompiledExpression>();

	public static int cacheHit = 0;
	public static int cacheMiss = 0;
	
	static {
		Class<?>[] staticLib=new Class[1];
		Class<?>[] dynamicLib = new Class[1];
		Class<?>[] dotLib = new Class[0];
		try{
			staticLib[0] = Class.forName("java.lang.Math");
			dynamicLib[0] = EvaluatorMap.class;
		}catch(Exception e){
			e.printStackTrace();
		}	
		
		map = new EvaluatorMap(); 
		evaluatedExpressions = new HashSet<String>();
		lib = new Library(staticLib, dynamicLib, dotLib, map, null);
		context = new Object[1];
		context[0] = map;
	}
	
	public static void addExpression(String exp){
		if(exp.startsWith("&")) exp = exp.substring(1);
		evaluatedExpressions.add(exp);
	}
	
	public static HashSet<String> getEvaluatedExpressions(){
		return evaluatedExpressions;
	}
	
	public static boolean isReferenced(String name){
		return evaluatedExpressions.contains(name);
	}
	
	public static void addVariable(String name, Object var){
		map.addVariable(name, var);
	}
	
	private static CompiledExpression compile(String expression) throws ParseException {
		try {
			CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib);
			cExpr.put(expression, c);
			return c;
		} catch (CompilationException e) {
			throw new ParseException("Cannot compile expression " + expression);
		} catch (Exception e){
			return null;
		}
	}

	private static CompiledExpression compile(String expression, Class<?> type) throws ParseException {
		try {
			CompiledExpression c = gnu.jel.Evaluator.compile(expression, lib, type);
			cExpr.put(expression + "@" + type.getSimpleName(), c);
			return c;
		} catch (CompilationException e) {
			throw new ParseException("Cannot compile expression " + expression);
		} catch (Exception e){
			return null;
		}
	}
	
	public static Number evaluateNumber(String expression) throws ExecutionException {
		Object o;
		try {
			CompiledExpression c = getCompiledExpression(expression);
			o = c.evaluate(context);
			if(o instanceof Number){
				return (Number) o;
			}else{
				throw new ExecutionException("Expression resulted in non-Number");
			}
		} catch (Throwable e) {
			throw new ExecutionException("Could not evaluate expression " + expression, e);
		}		
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
	
	public static char evaluateChar(String expression) throws ExecutionException{
		try {
			CompiledExpression c = getCompiledExpression(expression);
			return c.evaluate_char(context);
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
			}else{
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
	
	private static CompiledExpression getCompiledExpression(String expression, Class<?> type) throws ExecutionException {
		CompiledExpression c = cExpr.get(expression + "@" + type.getSimpleName());
		if(c == null){
			try {
				c = compile(expression, type);
			} catch (ParseException e) {
				throw new ExecutionException("Could not compile expression " + expression, e);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return c;
	}
	
	private static CompiledExpression getCompiledExpression(String expression) throws ExecutionException {
		CompiledExpression c = cExpr.get(expression);
		
		if(c == null){
			cacheMiss++;
			try {
				c = compile(expression);
			} catch (ParseException e) {
				throw new ExecutionException("Could not compile expression " + expression, e);
			} catch(Exception e){
				e.printStackTrace();
			}
		} else {
			cacheHit++;
		}
		return c;
	}
}
