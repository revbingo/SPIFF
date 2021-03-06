package com.revbingo.spiff.events;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.datatypes.IntegerInstruction;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

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

		Datatype xInst = new IntegerInstruction("x");
		xInst.setValue(Integer.valueOf(3));

		unit.notifyData(xInst);

		Datatype yInst = new IntegerInstruction("y");
		yInst.setValue(21);

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

		Datatype xInst = new IntegerInstruction("x");
		xInst.setValue(Integer.valueOf(3));

		unit.notifyData(xInst);

		Datatype yInst = new IntegerInstruction("y");
		yInst.setValue(21);

		unit.notifyData(yInst);

		unit.notifyGroup("inner", false);

		Datatype rootX = new IntegerInstruction("x");
		rootX.setValue(Integer.valueOf(30));
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
			Datatype xInst = new IntegerInstruction("x");
			xInst.setValue(Integer.valueOf(i));

			unit.notifyData(xInst);

			Datatype yInst = new IntegerInstruction("y");
			yInst.setValue(21);

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
		unit.setStrict(false);
		unit.setSkipUnboundGroups(true);

		IntegerInstruction intInst = new IntegerInstruction("x");
		intInst.setValue(10);

		unit.notifyData(intInst);
		unit.notifyGroup("unknown", true);
		unit.notifyGroup("stillUnknown", true);
		intInst.setValue(20);
		unit.notifyData(intInst);
		unit.notifyGroup("stillUnknown", false);
		intInst.setValue(30);
		unit.notifyData(intInst);
		unit.notifyGroup("unknown", false);

		RootBinding result = unit.getResult();
		assertThat(result.x, is(10));

	}

	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfStrictAndNoBindingForGroup() {
		ClassBindingEventListener<RootBinding> unit = new ClassBindingEventListener<RootBinding>(RootBinding.class);

		unit.setStrict(true);

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
