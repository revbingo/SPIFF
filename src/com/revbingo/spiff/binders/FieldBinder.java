/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
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

	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		Object newInstance;
		try {
			newInstance = boundField.getType().newInstance();
			this.bind(target, newInstance);
			return newInstance;
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		}
	}
	
}
