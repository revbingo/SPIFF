/*******************************************************************************
 * This file is part of SPIFF.
 * 
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
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
