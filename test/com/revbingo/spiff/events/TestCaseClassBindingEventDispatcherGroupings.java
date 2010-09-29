package com.revbingo.spiff.events;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;
import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.ReferencedInstruction;

public class TestCaseClassBindingEventDispatcherGroupings {

	@Test
	public void startingAndEndingGroupCreatesBoundObject() {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		unit.notifyGroup("inner", true);
		unit.notifyGroup("inner", false);
		
		RootBinding binding = unit.getResult();
		assertThat(binding, is(notNullValue()));
		
		assertThat(binding.inner, is(notNullValue()));
	}
	
	@Test
	public void fieldsSetOnInnerObjectWhenInGroup() {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
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
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
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
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		
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
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfStrictAndNoBindingForGroup() {
		ClassBindingEventDispatcher<RootBinding> unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
		
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
