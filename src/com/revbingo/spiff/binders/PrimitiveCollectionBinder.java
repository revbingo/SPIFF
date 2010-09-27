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

public class PrimitiveCollectionBinder implements Binder {

	private Field boundField;
	private FieldBinder boundFieldBinder;
	
	private static Map<Class<? extends Collection>, Class<? extends Collection>> preferredCollections 
	= new HashMap<Class<? extends Collection>, Class<? extends Collection>>();

	static {
		preferredCollections.put(List.class, ArrayList.class);
		preferredCollections.put(Set.class, HashSet.class);
		preferredCollections.put(Queue.class, LinkedList.class);
	}

	public PrimitiveCollectionBinder(Field f) {
		boundField = f;
		boundFieldBinder = new FieldBinder(boundField);
	}

	@Override
	public void bind(Object target, Object value) throws ExecutionException {
		try {
			Collection collection = (Collection) boundField.get(target);
			if(collection == null) {
				collection = this.instantiateCollection(boundField);
				boundFieldBinder.bind(target, collection);
			}
			
			collection.add(value);
		} catch (Exception e) {
			throw new ExecutionException("Could not get current value of field", e);
		}
	}
	

	private Collection<?> instantiateCollection(Field f) throws ExecutionException {
		Class<? extends Collection> collectionType = (Class<? extends Collection>) f.getType();
		if(collectionType.isInterface() && preferredCollections.containsKey(collectionType)) {
			collectionType = preferredCollections.get(collectionType);
		}
		
		try {
			return collectionType.newInstance();
		} catch (Exception e) {
			throw new ExecutionException("Could not instantiate collection for field " + f.getName(), e);
		}
	}


	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		throw new UnsupportedOperationException();
	}

}
