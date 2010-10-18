/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.ReferencedInstruction;

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

		ReferencedInstruction xInst = new IntegerInstruction();
		xInst.name = "x";
		xInst.value = new Integer(3);

		unit.notifyData(xInst);

		ReferencedInstruction yInst = new IntegerInstruction();
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

		ReferencedInstruction xInst = new IntegerInstruction();
		xInst.name = "x";
		xInst.value = new Integer(3);

		unit.notifyData(xInst);

		ReferencedInstruction yInst = new IntegerInstruction();
		yInst.name = "y";
		yInst.value = 21;

		unit.notifyData(yInst);

		unit.notifyGroup("inner", false);

		ReferencedInstruction rootX = new IntegerInstruction();
		rootX.name = "x";
		rootX.value = new Integer(30);
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
			ReferencedInstruction xInst = new IntegerInstruction();
			xInst.name = "x";
			xInst.value = new Integer(i);

			unit.notifyData(xInst);

			ReferencedInstruction yInst = new IntegerInstruction();
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
