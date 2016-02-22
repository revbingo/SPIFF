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

import java.util.Stack;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.datatypes.Datatype;

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
	public void notifyData(Datatype ins) {
		if(skipUnbound && skipCount > 0) return;
		Binder binder = bindingFactory.getBindingFor(ins.getName(), currentBinding.getClass());
		if(binder == null) {
			if(isStrict) {
				throw new ExecutionException("Could not get binding for instruction " + ins.getName());
			} else {
				return;
			}
		} else {
			binder.bind(currentBinding, ins.getValue());
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
