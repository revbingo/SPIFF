/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.events;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private final Map<Class<?>, Map<String, Binder>> binderCache = new HashMap<Class<?>, Map<String, Binder>>();

	public Binder getBindingFor(String name, Class<?> clazz) {
		Map<String, Binder> classCache = binderCache.get(clazz);
		if(classCache != null && classCache.containsKey(name)) {
			return classCache.get(name);
		}

		Binder b = null;
		for(Matcher matcher : matchers) {
			b = matcher.match(name, clazz);
			if(b != null) {
				break;
			}
		}
		addToCache(clazz, name, b);
		return b;
	}

	private void addToCache(Class<?> clazz, String name, Binder b) {
		Map<String, Binder> classCache = binderCache.get(clazz);
		if(classCache == null) {
			classCache = new HashMap<String, Binder>();
			binderCache.put(clazz, classCache);
		}
		classCache.put(name, b);
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
				return null;
			} catch (SecurityException e) {
				throw new ExecutionException("SecurityManager prevents access to class methods", e);
			}
		}
	}

	private static class BoundFieldMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			try {
				for (Field f : clazz.getDeclaredFields()) {
					if (f.isAnnotationPresent(Binding.class) && f.getAnnotation(Binding.class).value().equals(name)) {
						if (Collection.class.isAssignableFrom(f.getType())) {
							Class<?> genericType = null;
							Type fieldType = f.getGenericType();
							if (fieldType instanceof ParameterizedType) {
								genericType = (Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0];
							}

							if (primitiveTypes.contains(genericType)) {
								return new PrimitiveCollectionBinder(f);
							} else {
								return new ObjectCollectionBinder(f, genericType);
							}
						} else {
							return new FieldBinder(f);
						}
					} else if (f.isAnnotationPresent(BindingCollection.class)
							&& f.getAnnotation(BindingCollection.class).value().equals(name)) {

						Class<?> genericType = f.getAnnotation(BindingCollection.class).type();

						if (primitiveTypes.contains(genericType)) {
							return new PrimitiveCollectionBinder(f);
						} else {
							return new ObjectCollectionBinder(f, genericType);
						}
					}
				}
			} catch(SecurityException e) {
				throw new ExecutionException("SecurityManager prevents access to fields", e);
			}
			return null;
		}

	}

	private static class SetterMatcher extends Matcher {

		@Override
		public Binder match(String name, Class<?> clazz) {
			String setterName = createSetterName(name);
			for(Method m : clazz.getMethods()) {
				if(m.getName().equals(setterName)) {
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
			try {
				for(Field f : clazz.getDeclaredFields()) {
					if(getAnnotatedName(f) != null &&
							!(getAnnotatedName(f).equals("")) &&
							!(f.getName().equals(getAnnotatedName(f)))) continue;
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
			} catch(SecurityException e) {
				throw new ExecutionException("SecurityManager prevents access to fields", e);
			}
			return null;
		}

		private String getAnnotatedName(Field f) {
			BindingCollection bcAnno = f.getAnnotation(BindingCollection.class);
			if(bcAnno != null) return bcAnno.value();

			Binding bAnno = f.getAnnotation(Binding.class);
			if(bAnno != null) return bAnno.value();

			return null;
		}
	}


}
