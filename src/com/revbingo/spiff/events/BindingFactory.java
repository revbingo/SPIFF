package com.revbingo.spiff.events;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.binders.FieldBinder;
import com.revbingo.spiff.binders.MethodBinder;
import com.revbingo.spiff.binders.ObjectCollectionBinder;
import com.revbingo.spiff.binders.PrimitiveCollectionBinder;

public class BindingFactory {

	private static final List<Matcher> matchers = Arrays.asList(new BoundMethodMatcher(), new BoundFieldMatcher(), new SetterMatcher(), new FieldMatcher());
	
	private static final List<Class<?>> primitiveTypes = Arrays.asList(new Class<?>[] { Integer.class, Float.class, Double.class, Short.class, Long.class, Byte.class, Character.class, String.class});
	
	public Binder getBindingFor(String name, Class<?> clazz) {
		for(Matcher matcher : matchers) {
			Binder b = matcher.match(name, clazz);
			if(b != null) return b;
		}
		return null;
	}
	
	private abstract static class Matcher {
		public abstract Binder match(String name, Class<?> clazz) throws ExecutionException;
	}
	
	private static class BoundMethodMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			try {
				for(Method m : clazz.getMethods()) {
					if(m.isAnnotationPresent(Binding.class) && m.getAnnotation(Binding.class).value().equals(name)) {
						return new MethodBinder(m);
					}
				}
			} catch(SecurityException e) {
				throw new ExecutionException("SecurityManager prevents acces to class methods", e);
			}
			return null;
		}
	}
	
	private static class BoundFieldMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			
			for(Field f : clazz.getDeclaredFields()) {
				if(f.isAnnotationPresent(Binding.class) && f.getAnnotation(Binding.class).value().equals(name)) {
					if(Collection.class.isAssignableFrom(f.getType())) {
						Class<?> genericType = null;
						Type fieldType = f.getGenericType();
						if(fieldType instanceof ParameterizedType) {
							genericType = ((ParameterizedType) fieldType).getActualTypeArguments()[0].getClass();
						}
						
						if(primitiveTypes.contains(genericType)) {
							return new PrimitiveCollectionBinder(f);
						} else {
							return new ObjectCollectionBinder(f, genericType);
						}
					} else {
						return new FieldBinder(f);
					}
				} else if(f.isAnnotationPresent(BindingCollection.class) 
						&& (f.getAnnotation(BindingCollection.class).value().equals(name)
								| f.getName().equals(name))) {
					
					Class<?> genericType = f.getAnnotation(BindingCollection.class).type();
					if(genericType == null) {
						Type fieldType = f.getGenericType();
						if(fieldType instanceof ParameterizedType) {
							genericType = ((ParameterizedType) fieldType).getActualTypeArguments()[0].getClass();
						}
					}
					
					if(primitiveTypes.contains(genericType)) {
						return new PrimitiveCollectionBinder(f);
					} else {
						return new ObjectCollectionBinder(f, genericType);
					}
				}
			}
			return null;
		}
		
	}
	
	private static class SetterMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Method m : clazz.getMethods()) {
				if(m.getName().equals(createSetterName(name))) {
					return new MethodBinder(m);
				}
			}
			return null;
		}
		
		private String createSetterName(String str) {
			return "set" + str.substring(0,1).toUpperCase() + str.substring(1, str.length());
		}
	}
	
	private static class FieldMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Field f : clazz.getDeclaredFields()) {
				if(f.getName().equals(name)) {
					if(Collection.class.isAssignableFrom(f.getType())) {
						
						Class<?> genericType = null;
						Type fieldType = f.getGenericType();
						if(fieldType instanceof ParameterizedType) {
							genericType = (Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0];
						}
						
						if(primitiveTypes.contains(genericType)) {
							return new PrimitiveCollectionBinder(f);
						} else {
							return new ObjectCollectionBinder(f, genericType);
						}
					}
					return new FieldBinder(f);
				}
			}
			return null;
		}
		
	}
}
