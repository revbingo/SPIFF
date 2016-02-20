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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.IntegerInstruction;

public class TestCaseClassBindingEventDispatcherGroupings {

	@Test
	public void startingAndEndingGroupCreatesBoundObject() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);
		unit.notifyGroup("inner", true);
		unit.notifyGroup("inner", false);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));

		assertThat(binding.inner, is(notNullValue()));
	}

	@Test
	public void fieldsSetOnInnerObjectWhenInGroup() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);
		unit.notifyGroup("inner", true);

		Datatype xInst = new IntegerInstruction();
		xInst.name = "x";
		xInst.value = Integer.valueOf(3);

		unit.notifyData(xInst);

		Datatype yInst = new IntegerInstruction();
		yInst.name = "y";
		yInst.value = 21;

		unit.notifyData(yInst);

		unit.notifyGroup("inner", false);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.inner, is(notNullValue()));
		assertThat(binding.inner.x, is(3));
		assertThat(binding.inner.y, is(21));
	}

	@Test
	public void restoresPreviousBindingAtEndOfGroup() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);
		unit.notifyGroup("inner", true);

		Datatype xInst = new IntegerInstruction();
		xInst.name = "x";
		xInst.value = new Integer(3);

		unit.notifyData(xInst);

		Datatype yInst = new IntegerInstruction();
		yInst.name = "y";
		yInst.value = 21;

		unit.notifyData(yInst);

		unit.notifyGroup("inner", false);

		Datatype rootX = new IntegerInstruction();
		rootX.name = "x";
		rootX.value = Integer.valueOf(30);
		unit.notifyData(rootX);

		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.inner, is(notNullValue()));
		assertThat(binding.inner.x, is(3));
		assertThat(binding.inner.y, is(21));
		assertThat(binding.x, is(30));
	}

	@Test
	public void bindingToCollectionOfObjects() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);

		for(int i = 0; i < 3; i++) {
			unit.notifyGroup("listOfObjects", true);
			Datatype xInst = new IntegerInstruction();
			xInst.name = "x";
			xInst.value = Integer.valueOf(i);

			unit.notifyData(xInst);

			Datatype yInst = new IntegerInstruction();
			yInst.name = "y";
			yInst.value = 21;

			unit.notifyData(yInst);
			unit.notifyGroup("listOfObjects", false);
		}

		RootBinding binding = unit.getResult();
		assertThat(binding.listOfObjects, is(notNullValue()));
		assertThat(binding.listOfObjects.size(), is(3));
		assertThat(binding.listOfObjects.get(0), instanceOf(InnerBinding.class));
		assertThat(binding.listOfObjects.get(0).x, is(0));
		assertThat(binding.listOfObjects.get(1), instanceOf(InnerBinding.class));
		assertThat(binding.listOfObjects.get(1).x, is(1));
		assertThat(binding.listOfObjects.get(2), instanceOf(InnerBinding.class));
		assertThat(binding.listOfObjects.get(2).x, is(2));
	}

	@Test
	public void skipGroupsPreventsDataBeingNotified() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);
		unit.isStrict(false);
		unit.setSkipUnboundGroups(true);

		IntegerInstruction intInst = new IntegerInstruction();
		intInst.name = "x";
		intInst.value = 10;

		unit.notifyData(intInst);
		unit.notifyGroup("unknown", true);
		unit.notifyGroup("stillUnknown", true);
		intInst.value = 20;
		unit.notifyData(intInst);
		unit.notifyGroup("stillUnknown", false);
		intInst.value = 30;
		unit.notifyData(intInst);
		unit.notifyGroup("unknown", false);

		RootBinding result = unit.getResult();
		assertThat(result.x, is(10));

	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfStrictAndNoBindingForGroup() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);

		unit.isStrict(true);

		unit.notifyGroup("unbound", true);
	}

	public static class RootBinding {
		public InnerBinding inner;
		public int x;

		@BindingCollection(type=InnerBinding.class)
		public List<InnerBinding> listOfObjects;
	}

	public static class InnerBinding {

		public int x;
		public int y;
		public ReallyInnerBinding reallyInner;
	}

	public static class ReallyInnerBinding {
		public int a;
		public int b;
	}

}
