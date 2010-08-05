package com.revbingo.spiff.bindings;

import java.util.HashMap;
import java.util.Map;

import com.revbingo.spiff.ExecutionException;

public abstract class BindingWrapper {

	private Map<String, BindingWrapper> childBindings;
	
	public abstract void attachTo(BindingWrapper container) throws ExecutionException;
	public abstract void attach(BindingWrapper child) throws ExecutionException;
	public abstract void setValue(Object value) throws ExecutionException;
	public abstract Class<?> getType();
	public abstract void instantiate() throws ExecutionException;
	public abstract Object getBoundObject();
	public abstract BindingWrapper createFromTemplate();
	
	public void addBinding(String name, BindingWrapper binding) {
		if(childBindings == null) childBindings = new HashMap<String, BindingWrapper>();
		
		childBindings.put(name, binding);
	}
	
	public boolean hasBindingFor(String name) {
		if(childBindings == null) return false;
		return childBindings.containsKey(name);
	}
	
	public BindingWrapper getBindingFor(String name) {
		return childBindings.get(name);
	}
	
}