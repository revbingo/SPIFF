package com.revbingo.spiff.bindings;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.util.MethodDispatcher;

public class FieldBindingWrapper extends BindingWrapper {

	protected Field field;
	protected Object instance;
	
	public FieldBindingWrapper(Field f) {
		this.field = f;
	}
	
	@Override
	public Class<?> getType() {
		return field.getType();
	}
	
	@Override
	public Object getBoundObject() {
		return instance;
	}

	@Override
	public void instantiate() throws ExecutionException {
		try {
			instance = field.getType().newInstance();
		} catch(Exception e) {
			throw new ExecutionException("Could not instantiate object of type " + field.getType().getName(), e);
		}
	}

	@Override
	public void attachTo(BindingWrapper container) throws ExecutionException {
		Object receiver = container.getBoundObject();
		Object value = instance;
		try {
			if((field.getModifiers() & Modifier.PUBLIC) == Modifier.PUBLIC) {
				field.set(receiver, value);
			} else {
				MethodDispatcher.dispatchSetter(field.getName(), receiver, value);
			}
		} catch (Exception e) {
			throw new ExecutionException("Could not call setter on " + receiver.getClass().getName() + " for value " + value, e);
		}
	}
	
	@Override
	public void setValue(Object value) throws ExecutionException {
		instance = value;
	}

	@Override
	public void attach(BindingWrapper child) throws ExecutionException {
		throw new UnsupportedOperationException();
	}

	@Override
	public BindingWrapper createFromTemplate() {
		return new FieldBindingWrapper(this.field);
	}
}
