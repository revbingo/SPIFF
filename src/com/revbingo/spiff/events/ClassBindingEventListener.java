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
package com.revbingo.spiff.events;

import java.util.Stack;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.instructions.ReferencedInstruction;

public class ClassBindingEventListener<T> implements EventListener {

	private T rootBinding;
	private Object currentBinding;
	private Stack<Object> bindingStack;

	private boolean isStrict = true;

	private BindingFactory bindingFactory = new BindingFactory();
	private boolean skipUnbound;
	private int skipCount;

	public ClassBindingEventListener(Class<T> clazz) {
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
		if(skipUnbound && skipCount > 0) return;
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
					if(skipUnbound) skipCount++;
					return;
				}
			} else {
				currentBinding = binder.createAndBind(currentBinding);
			}
		} else {
			if(skipUnbound && skipCount > 0) skipCount--;
			currentBinding = bindingStack.pop();
		}
	}

	public T getResult() {
		return rootBinding;
	}

	public void setSkipUnboundGroups(boolean b) {
		this.skipUnbound = b;
	}

}
