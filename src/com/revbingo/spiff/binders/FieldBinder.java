package com.revbingo.spiff.binders;

import java.lang.reflect.Field;

import com.revbingo.spiff.ExecutionException;

public class FieldBinder implements Binder {

	private Field boundField;
	
	public FieldBinder(Field f) {
		boundField = f;
		boundField.setAccessible(true);
	}

	@Override
	public void bind(Object target, Object value) throws ExecutionException {
		try {
			boundField.set(target, value);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException("Wrong type for field " + boundField.getName() + " (expected " + boundField.getType().getSimpleName() + ", got " + value.getClass().getSimpleName() + ")");
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Cannot access field " + boundField.getName() + " (is it final static?)");
		}
	}
	
}
