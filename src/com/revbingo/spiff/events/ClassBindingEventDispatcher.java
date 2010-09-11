package com.revbingo.spiff.events;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.instructions.ReferencedInstruction;
import com.revbingo.spiff.util.MethodDispatcher;

public class ClassBindingEventDispatcher<T> implements EventDispatcher {

	private T rootBinding;
	
	public ClassBindingEventDispatcher(Class<T> clazz) {
		try {
			this.rootBinding = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate " + clazz.getCanonicalName(), e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not access " + clazz.getCanonicalName(), e);
		}
	}

	@Override
	public void notifyData(ReferencedInstruction ins) {
		try {
			Field f = rootBinding.getClass().getDeclaredField(ins.name);
			this.setFieldValue(f, ins.value);
		} catch (SecurityException e) {
			throw new ExecutionException("SecurityManager prevents access to field " + ins.name, e);
		} catch (NoSuchFieldException e) {
			try {
				MethodDispatcher.dispatchSetter(ins.name, rootBinding, ins.value);
			} catch (NoSuchMethodException e1) {
				boolean dispatched = false;
				for(Field f : rootBinding.getClass().getDeclaredFields()) {
					if(f.isAnnotationPresent(Binding.class)) {
						Binding b = f.getAnnotation(Binding.class);
						if(b.value().equals(ins.name)) {
							if(!f.isAccessible()) f.setAccessible(true);
							setFieldValue(f, ins.value);
							dispatched = true;
						}
					}
				}
				if(!dispatched) throw new ExecutionException("Could not find field " + ins.name, e);
			} catch (IllegalAccessException e1) {
				//Hmm, how to test?
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				throw new ExecutionException("Exception occurred invoking setter for " + ins.name);
			}
		}
	}
	
	public void setFieldValue(Field f, Object value) {
		try {
			if(!f.isAccessible()) f.setAccessible(true);
			f.set(rootBinding, value);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException("Wrong type " + value.getClass().getCanonicalName() + " for field " + f.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not access field " + f.getName(), e);
		}
	}

	@Override
	public void notifyGroup(String groupName, boolean start) {
		throw new UnsupportedOperationException();
	}

	public T getBoundValue() {
		return rootBinding;
	}

}
