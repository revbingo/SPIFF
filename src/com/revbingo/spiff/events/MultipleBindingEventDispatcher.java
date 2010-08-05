package com.revbingo.spiff.events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.bindings.BindingFactory;
import com.revbingo.spiff.bindings.BindingWrapper;
import com.revbingo.spiff.bindings.CollectionBindingWrapper;


public class MultipleBindingEventDispatcher extends BindingEventDispatcher<ArrayList<?>> {

	public MultipleBindingEventDispatcher(Class<?>... classes) {
		if(classes == null || classes.length == 0) throw new IllegalArgumentException("Must specify at least one class to bind with");
		
		for(Class<?> c : classes) {
			discoverBindings(null, c);
		}
		currentBinding = new StandaloneBindingWrapper();
	}
	
	public static MultipleBindingEventDispatcher getInstance(Class<?>... classes) {
		return new MultipleBindingEventDispatcher(classes);
	}
	
	public class StandaloneBindingWrapper extends BindingWrapper {

		private List<Object> objects = new ArrayList<Object>();
		
		@Override
		public void attachTo(BindingWrapper container)
				throws ExecutionException {
			objects.add(container.getBoundObject());
		}

		@Override
		public Object getBoundObject() {
			return objects;
		}

		@Override
		public Class<?> getType() {
			return ArrayList.class;
		}

		@Override
		public void instantiate() throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setValue(Object value) throws ExecutionException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void attach(BindingWrapper child) throws ExecutionException {
			objects.add(child.getBoundObject());
		}
		
		public BindingWrapper createFromTemplate() {
			return new StandaloneBindingWrapper();
		}
	}
}