package com.revbingo.spiff.events;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.instructions.ReferencedInstruction;
import com.revbingo.spiff.util.MethodDispatcher;

public class ClassBindingEventDispatcher<T> implements EventDispatcher {

	private T rootBinding;
	private Object currentBinding;
	private Stack<Object> bindingStack;
	
	private boolean isStrict = true;
	
	private BindingFactory bindingFactory = new BindingFactory();
	
	public ClassBindingEventDispatcher(Class<T> clazz) {
		try {
			this.rootBinding = clazz.newInstance();
			this.currentBinding = this.rootBinding;
			
			bindingStack = new Stack<Object>();
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate " + clazz.getCanonicalName(), e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not access " + clazz.getCanonicalName(), e);
		}
	}

	public void isStrict(boolean isStrict) {
		this.isStrict = isStrict;
	}

	@Override
	public void notifyData(ReferencedInstruction ins) {
		Binder binder = bindingFactory.getBindingFor(ins.name, currentBinding.getClass());
		if(binder == null) {
			if(isStrict) {
				throw new ExecutionException("Could not get binding for instruction " + ins.name);
			} else {
				return;
			}
		} else {
			binder.bind(currentBinding, ins.value);
		}
	}

	@Override
	public void notifyGroup(String groupName, boolean start) throws ExecutionException {
		if(start) {
			bindingStack.push(currentBinding);
			
			Binder binder = bindingFactory.getBindingFor(groupName, currentBinding.getClass());
			if(binder == null) {
				if(isStrict) {
					throw new ExecutionException("Could not get binding for group " + groupName);
				} else {
					return;
				}
			} else {
				currentBinding = binder.createAndBind(currentBinding);
			}
		} else {
			currentBinding = bindingStack.pop();
		}
	}

	public T getResult() {
		return rootBinding;
	}

}
