package com.revbingo.spiff.bindings;

import com.revbingo.spiff.ExecutionException;

public class ClassBindingWrapper extends BindingWrapper {

	private Class<?> clazz;
	private Object instance;
	
	public ClassBindingWrapper(Class<?> c) {
		this.clazz = c;
	}
	
	@Override
	public Class<?> getType() {
		return clazz;
	}

	@Override
	public Object getBoundObject() {
		return instance;
	}

	@Override
	public void instantiate() throws ExecutionException {
		try {
			instance = clazz.newInstance();
		} catch (Exception e) {
			throw new ExecutionException("Could not instantiate new object of type " + clazz.getName(), e);
		}
	}

	@Override
	public void attachTo(BindingWrapper container) throws ExecutionException {
		container.attach(this);
	}
	
	@Override
	public void setValue(Object value) throws ExecutionException {
		throw new UnsupportedOperationException("Cannot call setValue on CollectionBindingWrapper");
	}

	@Override
	public void attach(BindingWrapper child) throws ExecutionException {
		child.attachTo(this);
	}

	@Override
	public BindingWrapper createFromTemplate() {
		return new ClassBindingWrapper(this.clazz);
	}
	
	
}
