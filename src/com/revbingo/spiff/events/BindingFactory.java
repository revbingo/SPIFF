package com.revbingo.spiff.events;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.binders.CollectionBinder;
import com.revbingo.spiff.binders.FieldBinder;
import com.revbingo.spiff.binders.MethodBinder;
import com.revbingo.spiff.util.MethodDispatcher;

public class BindingFactory {

	public Binder getBindingFor(String name, Class<?> clazz) {
		List<Matcher> matchers = Arrays.asList(new BoundMethodMatcher(), new BoundFieldMatcher(), new SetterMatcher(), new FieldMatcher());
		
		for(Matcher matcher : matchers) {
			Binder b = matcher.match(name, clazz);
			if(b != null) return b;
		}
		return null;
	}
	
	private abstract static class Matcher {
		public abstract Binder match(String name, Class<?> clazz);
	}
	
	private static class BoundMethodMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Method m : clazz.getMethods()) {
				if(m.isAnnotationPresent(Binding.class) && m.getAnnotation(Binding.class).value().equals(name)) {
					return new MethodBinder();
				}
			}
			return null;
		}
		
	}
	
	private static class BoundFieldMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Field f : clazz.getDeclaredFields()) {
				if(f.isAnnotationPresent(Binding.class) && f.getAnnotation(Binding.class).value().equals(name)) {
					return new FieldBinder(f);
				}
			}
			return null;
		}
		
	}
	
	private static class SetterMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Method m : clazz.getMethods()) {
				if(m.getName().equals("set" + MethodDispatcher.capitalise(name))) {
					return new MethodBinder();
				}
			}
			return null;
		}
		
	}
	
	private static class FieldMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			for(Field f : clazz.getDeclaredFields()) {
				if(f.getName().equals(name)) {
					if(Collection.class.isAssignableFrom(f.getType())) {
						return new CollectionBinder();
					}
					return new FieldBinder(f);
				}
			}
			return null;
		}
		
	}
}
