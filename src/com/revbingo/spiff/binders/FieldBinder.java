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
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
