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
			Collection collection = (Collection) boundField.get(target);
			if(collection == null) {
				collection = instantiateCollection(boundField);
				boundFieldBinder.bind(target, collection);
			}
			if(collectedType != null) {
			
					Object newInstance = collectedType.newInstance();
					collection.add(newInstance);
					return newInstance;
				
			} else {
				return target;
			}
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		}
	}
}
