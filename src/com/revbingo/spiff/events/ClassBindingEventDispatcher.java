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
	
	private static Map<Class<?>, Class<?>> preferredCollections = new HashMap<Class<?>, Class<?>>();
	
	static {
		preferredCollections.put(List.class, ArrayList.class);
		preferredCollections.put(Set.class, HashSet.class);
		preferredCollections.put(Queue.class, LinkedList.class);
	}
	
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

	public void setFieldValue(Field f, Object value) {
		try {
			if(!f.isAccessible()) f.setAccessible(true);
			Class<?> fieldClass = f.getType();
			if(Collection.class.isAssignableFrom(fieldClass)) {
				Object fieldObj = f.get(currentBinding);
				if(fieldObj == null) {
					if(fieldClass.isInterface() && preferredCollections.containsKey(fieldClass)) {
						fieldClass = preferredCollections.get(fieldClass);
					}
					fieldObj = fieldClass.newInstance();
					f.set(currentBinding, fieldObj);
				}
				@SuppressWarnings("unchecked") Collection<Object> collection = (Collection<Object>) fieldObj;
				collection.add(value);
			} else {
				f.set(currentBinding, value);
			}
		} catch (IllegalArgumentException e) {
			throw new ExecutionException("Wrong type " + value.getClass().getCanonicalName() + " for field " + f.getName(), e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not access field " + f.getName(), e);
		} catch (InstantiationException e) {
			throw new ExecutionException("Tried to instantiate non-concrete type " + f.getType(), e);
		}
	}

	@Override
	public void notifyGroup(String groupName, boolean start) throws ExecutionException {
//		try {
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
//				Field targetBindingField;
//				try {
//					targetBindingField = currentBinding.getClass().getDeclaredField(groupName);
//				} catch (NoSuchFieldException e) {
//					targetBindingField = findFieldFor(groupName);
//					if(targetBindingField == null && isStrict) {
//						throw new ExecutionException("Could not find field for binding " + groupName, e);
//					} else if (targetBindingField == null && !isStrict) {
//						return;
//					}
//				}
//				if(!targetBindingField.isAccessible()) targetBindingField.setAccessible(true);
//				Class<?> targetType = targetBindingField.getType();
//				if(Collection.class.isAssignableFrom(targetType)) {
//					Collection<Object> collection = (Collection<Object>) targetBindingField.get(currentBinding);
//					if(collection == null) {
//						if(targetType.isInterface() && preferredCollections.containsKey(targetType)) {
//							targetType = preferredCollections.get(targetType);
//						}
//						collection = (Collection<Object>) targetType.newInstance();
//						targetBindingField.set(currentBinding, collection);
//					}
//					
//					Object newInstance = targetBindingField.getAnnotation(BindingCollection.class).type().newInstance();
//					collection.add(newInstance);
//					currentBinding = newInstance;
//				} else {
//					Object targetObject = targetBindingField.get(currentBinding);
//					if(targetObject == null) {
//						Object newInstance = targetBindingField.getType().newInstance();
//						targetBindingField.set(currentBinding, newInstance);
//						currentBinding = newInstance;
//					}
//				}
			} else {
				currentBinding = bindingStack.pop();
			}
//		} catch (SecurityException e) {
//			throw new ExecutionException("", e);
//		} catch (IllegalArgumentException e) {
//			throw new ExecutionException("", e);
//		} catch (IllegalAccessException e) {
//			throw new ExecutionException("", e);
//		} catch (InstantiationException e) {
//			throw new ExecutionException("", e);
//		}
		
	}

	public T getResult() {
		return rootBinding;
	}

}
