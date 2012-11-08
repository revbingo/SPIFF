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
package com.revbingo.spiff.binders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.revbingo.spiff.ExecutionException;

public class MethodBinder implements Binder {

	private Method boundMethod;

	public MethodBinder(Method m) {
		boundMethod = m;
		boundMethod.setAccessible(true);
	}

	@Override
	public void bind(Object target, Object value) throws ExecutionException {
		try {
			boundMethod.invoke(target, value);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException("Method " + boundMethod.getName() + " called with wrong arg type (tried " + (value == null ? "null" : value.getClass().getSimpleName()) + ")", e);
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Method " + boundMethod.getName() + " cannot be accessed", e);
		} catch (InvocationTargetException e) {
			throw new ExecutionException("An exception was thrown when invoking " + boundMethod.getName(), e);
		}
	}

	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		throw new ExecutionException("Cannot bind group to a method");
	}
}
