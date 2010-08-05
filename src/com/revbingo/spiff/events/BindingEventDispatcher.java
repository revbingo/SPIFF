package com.revbingo.spiff.events;

import java.lang.reflect.Field;
import java.util.Stack;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.bindings.BindingWrapper;
import com.revbingo.spiff.bindings.ClassBindingWrapper;
import com.revbingo.spiff.bindings.CollectionBindingWrapper;
import com.revbingo.spiff.bindings.FieldBindingWrapper;
import com.revbingo.spiff.instructions.ReferencedInstruction;

public class BindingEventDispatcher<T> implements EventDispatcher {

	protected BindingWrapper currentBinding = null;
	protected Stack<BindingWrapper> bindingStack = new Stack<BindingWrapper>();
	protected boolean strictMode;
	protected BindingWrapper rootBinding;
	
	protected BindingEventDispatcher() {}
	
	private BindingEventDispatcher(Class<T> rootClass) {
		strictMode = false;
		rootBinding = new ClassBindingWrapper(rootClass);
		try {
			rootBinding.instantiate();
		} catch (ExecutionException e) {
			throw new RuntimeException("Could not instantiate root binding");
		}
		discoverBindings(rootBinding, rootClass);
		currentBinding = rootBinding;
	}
	
	public static <V> BindingEventDispatcher<V> getInstance(Class<V> rootClass) {
		return new BindingEventDispatcher<V>(rootClass);
	}
	
	@Override
	public void notifyData(ReferencedInstruction ins) {
		try {
			BindingWrapper valueBinding = null;
			
			if(currentBinding.hasBindingFor(ins.name)) {
				valueBinding = currentBinding.getBindingFor(ins.name);
			} else {
				if(currentBinding == null) return;
				try {
					Field targetField = currentBinding.getType().getDeclaredField(ins.name);
					valueBinding = new FieldBindingWrapper(targetField);
				} catch (NoSuchFieldException e) {
					if(strictMode) throw e;
					return;
				}
			}
			
			valueBinding.setValue(ins.value);
			valueBinding.attachTo(currentBinding);
		} catch (Exception e) {
			throw new RuntimeException("Could not invoke setter for " + ins.name + " on object " + currentBinding.getBoundObject().getClass().getName() + " with " + ins.value.getClass().getName(), e);
		}
		
	}

	@Override
	public void notifyGroup(String groupName, boolean start) {
		try {
			if(start) {
				if(currentBinding.hasBindingFor(groupName)) {
					BindingWrapper newBinding = currentBinding.getBindingFor(groupName);
					newBinding.instantiate();
					
					if(currentBinding != null) {
						newBinding.attachTo(currentBinding);
						bindingStack.push(currentBinding);
					}
					currentBinding = newBinding;
					
				} else {
					if(strictMode) throw new ExecutionException("No binding for group " + groupName);
					return;
				}
			} else {
				if(!bindingStack.isEmpty()) currentBinding = bindingStack.pop();
			}
		} catch(Exception e) {
			throw new RuntimeException("Error occurred " + e.getMessage());
		}
	}
	
	public T getResult() {
		return (T) currentBinding.getBoundObject();
	}
	
	void discoverBindings(BindingWrapper parentBinding, Class<?> c) {
		for(Field f : c.getDeclaredFields()) {
			if(f.isAnnotationPresent(Binding.class)) {
				Binding b = f.getAnnotation(Binding.class);
				BindingWrapper binding = new FieldBindingWrapper(f);
				parentBinding.addBinding(b.value(), binding);
				discoverBindings(binding, f.getType());
			} else if(f.isAnnotationPresent(BindingCollection.class)) {
				BindingCollection b = f.getAnnotation(BindingCollection.class);
				BindingWrapper binding = null;
				try {
					binding = new CollectionBindingWrapper(f, f.getType(), b.type());
					parentBinding.addBinding(b.value(), binding );
				} catch (Exception e) {
					e.printStackTrace();
				}
				discoverBindings(binding, b.type());
			}
		}
	}
	
	public boolean isStrict() {
		return strictMode;
	}

	public void isStrict(boolean strictMode) {
		this.strictMode = strictMode;
	}

}
