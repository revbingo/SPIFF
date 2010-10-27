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

import java.lang.reflect.Field;
import java.util.Collection;

import com.revbingo.spiff.ExecutionException;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ObjectCollectionBinder extends PrimitiveCollectionBinder {

	private Class<?> collectedType;

	public ObjectCollectionBinder(Field f, Class<?> clazz) {
		super(f);
		collectedType = clazz;
	}

	@Override
	public Object createAndBind(Object target) throws ExecutionException {
		try {
			Collection collection = ensureCollectionExists(boundField, target);
			Object newInstance = collectedType.newInstance();
			collection.add(newInstance);
			return newInstance;
		} catch (InstantiationException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		} catch (IllegalAccessException e) {
			throw new ExecutionException("Could not instantiate new instance of " + boundField.getType().getSimpleName());
		}
	}
}
