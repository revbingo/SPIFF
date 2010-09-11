package com.revbingo.spiff.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MethodDispatcher {

	private static Map<Class<?>, Class<?>> alternativeTypes;
	
	static {
		alternativeTypes = new HashMap<Class<?>, Class<?>>();
		alternativeTypes.put(Short.class, short.class);
		alternativeTypes.put(Byte.class, byte.class);
		alternativeTypes.put(Integer.class, int.class);
		alternativeTypes.put(Long.class, long.class);
		alternativeTypes.put(Boolean.class, boolean.class);
		alternativeTypes.put(Double.class, double.class);
		alternativeTypes.put(Float.class, float.class);
		
		alternativeTypes.put(short.class, Short.class);
		alternativeTypes.put(byte.class, Byte.class);
		alternativeTypes.put(int.class, Integer.class);
		alternativeTypes.put(long.class, Long.class);
		alternativeTypes.put(boolean.class, Boolean.class);
		alternativeTypes.put(double.class, Double.class);
		alternativeTypes.put(float.class, Float.class);
	}
	
	public static Object dispatchSetter(String fieldName, Object receiver, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		return dispatch("set" + capitalise(fieldName), receiver, params);
	}
	
	public static Object dispatch(String methodName, Object receiver, Object... params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method m = findMethod(methodName, receiver, getClasses(params));
		return m.invoke(receiver, params);
	}
	
	private static String capitalise(String str) {
		return str.substring(0,1).toUpperCase() + str.substring(1, str.length());
	}
	
	static Class<?>[] getClasses(Object... params) {
		if(params == null) return null;
		Class<?>[] result = new Class<?>[params.length];
		for (int i = 0; i < params.length; i++) {
			if(params[i] == null) continue;
			result[i] = params[i].getClass();
		}
		return result;
	}
	
	static Method findMethod(String methodName, Object receiver, Class<?>... paramTypes) throws NoSuchMethodException {
		try {
			return receiver.getClass().getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			paramTypes = getAlternativeTypes(paramTypes);
			return receiver.getClass().getMethod(methodName, paramTypes);
		}
	}
	
	static Class<?>[] getAlternativeTypes(Class<?>... paramTypes) {
		Class<?>[] result = new Class<?>[paramTypes.length];
		for(int i = 0; i < paramTypes.length; i++) {
			if(alternativeTypes.containsKey(paramTypes[i])) {
				result[i] = alternativeTypes.get(paramTypes[i]);
			} else {
				result[i] = paramTypes[i];
			}
		}
		return result;
	}
}
