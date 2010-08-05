package com.revbingo.spiff.bindings;

import java.util.HashMap;
import java.util.Map;

public class BindingFactory {

	Map<String, BindingWrapper> bindings;
	
	public BindingFactory() {
		bindings = new HashMap<String, BindingWrapper>();
	}
	
	public boolean binds(String name) {
		return bindings.containsKey(name);
	}
	
	public BindingWrapper get(String name) {
		return bindings.get(name).createFromTemplate();
	}
	
	public void put(String name, BindingWrapper binding) {
		bindings.put(name, binding);
	}
	
	public Map<String, BindingWrapper> getBindings() {
		return bindings;
	}
	
	public void setBindings(Map<String, BindingWrapper> bindings) {
		this.bindings = bindings;
	}
}
