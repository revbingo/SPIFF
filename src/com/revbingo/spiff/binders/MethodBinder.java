package com.revbingo.spiff.binders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.revbingo.spiff.ExecutionException;

public class MethodBinder implements Binder {

	private Method boundMethod;
	
	public MethodBinder(Method m) {
		boundMethod = m;
		boundMethod.setAccessible(true);
	}
	
	@Override
	public void bind(Object target, Object value) throws ExecutionException {
		try {
			boundMethod.invoke(target, value);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException("Method " + boundMethod.getName() + " called with wrong arg type (expected " + boundMethod.getParameterTypes()[0] + ", got " + (value == null ? "null" : value.getClass().getSimpleName()) + ")", e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Method " + boundMethod.getName() + " cannot be accessed", e);
		} catch (InvocationTargetException e) {
			throw new ExecutionException("An exception was thrown when invoking " + boundMethod.getName(), e);
		}
	}

	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		throw new ExecutionException("Cannot bind group to a method");
	}
}
