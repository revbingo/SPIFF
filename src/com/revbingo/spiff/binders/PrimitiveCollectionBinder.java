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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.revbingo.spiff.ExecutionException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PrimitiveCollectionBinder implements Binder {

	protected Field boundField;
	protected FieldBinder boundFieldBinder;

	protected static Map<Class<? extends Collection>, Class<? extends Collection>> preferredCollections
										= new HashMap<Class<? extends Collection>, Class<? extends Collection>>();

	static {
		preferredCollections.put(List.class, ArrayList.class);
		preferredCollections.put(Set.class, HashSet.class);
		preferredCollections.put(Queue.class, LinkedList.class);
	}

	public PrimitiveCollectionBinder(Field f) {
		if(!Collection.class.isAssignableFrom(f.getType())) throw new ExecutionException("Cannot create CollectionBinder for non-Collection class");
		boundField = f;
		boundFieldBinder = new FieldBinder(boundField);
	}

	@Override
	public void bind(Object target, Object value) throws ExecutionException {
		Collection collection = this.ensureCollectionExists(boundField, target);
		collection.add(value);
	}

	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		throw new ExecutionException("Cannot bind group to a primitive");
	}

	protected Collection<?> ensureCollectionExists(Field f, Object target) throws ExecutionException {
		try {
			Collection collection = (Collection) boundField.get(target);
			if(collection != null) return collection;

			Class<? extends Collection> collectionType = (Class<? extends Collection>) f.getType();
			if(collectionType.isInterface() && preferredCollections.containsKey(collectionType)) {
				collectionType = preferredCollections.get(collectionType);
			}

			collection = collectionType.newInstance();
			boundFieldBinder.bind(target, collection);
			return collection;
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not get current value of field", e);
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		}

	}
}
