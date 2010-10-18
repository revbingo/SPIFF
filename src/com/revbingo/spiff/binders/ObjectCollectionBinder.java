/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.binders;

import java.lang.reflect.Field;
import java.util.Collection;

import com.revbingo.spiff.ExecutionException;

public class ObjectCollectionBinder extends PrimitiveCollectionBinder {

	private Class<?> collectedType;

	public ObjectCollectionBinder(Field f, Class<?> clazz) {
		super(f);
		collectedType = clazz;
	}
	
	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		try {
			Collection collection = ensureCollectionExists(boundField, target);
			Object newInstance = collectedType.newInstance();
			collection.add(newInstance);
			return newInstance;
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		}
	}
}
