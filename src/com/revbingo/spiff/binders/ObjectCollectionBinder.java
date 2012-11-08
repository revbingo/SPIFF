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
package com.revbingo.spiff.binders;

import java.lang.reflect.Field;
import java.util.Collection;

import com.revbingo.spiff.ExecutionException;

@SuppressWarnings({"rawtypes", "unchecked"})
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
