package com.revbingo.spiff.bindings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.revbingo.spiff.ExecutionException;

public class CollectionBindingWrapper<T> extends FieldBindingWrapper {

	private T enclosedInstance;
	private Class<? extends Collection<?>> collectionType;
	private Class<T> enclosedType;
	
	private static Map<Class<?>, Class<?>> collectionImplementations
			= new HashMap<Class<?>,Class<?>>();
	
	static {
		collectionImplementations.put(List.class, ArrayList.class);
		collectionImplementations.put(Set.class, HashSet.class);
		collectionImplementations.put(SortedSet.class, TreeSet.class);
		collectionImplementations.put(SortedMap.class, TreeMap.class);
	}
	
	public CollectionBindingWrapper(Field f, Class<? extends Collection<?>> collectionType, Class<T> enclosedType) throws ExecutionException {
		super(f);
		this.collectionType = collectionType;
		this.enclosedType = enclosedType;
		try {
			Class<?> concreteType = getConcreteImplementationType(collectionType);
			instance = concreteType.newInstance();
		} catch (Exception e) {
			throw new ExecutionException("Cannot instantiate new collection of type " + collectionType.getName(), e);
		}
	}
	
	@Override
	public Class<?> getType() {
		return enclosedType;
	}

	@Override
	public Object getBoundObject() {
		return enclosedInstance;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void instantiate() throws ExecutionException {
		try {
			enclosedInstance = enclosedType.newInstance();
			((Collection<T>) instance).add(enclosedInstance);
		} catch (Exception e) {
			throw new ExecutionException("Cannot instantiate new collection of type " + collectionType.getName(), e);
		}
	}

	@Override
	public void setValue(Object value) throws ExecutionException { 
		((Collection<T>) instance).add((T) value);
	}
	
	@Override
	public void attach(BindingWrapper child) throws ExecutionException {
		setValue(child.getBoundObject());
	}
	
	private Class<?> getConcreteImplementationType(Class<?> collectionType) {
		Class<?> concreteType = collectionImplementations.get(collectionType);
		return (concreteType == null) ? collectionType : concreteType;
	}

	@Override
	public BindingWrapper createFromTemplate() {
		try {
			return new CollectionBindingWrapper(this.field, this.collectionType, this.enclosedType);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
