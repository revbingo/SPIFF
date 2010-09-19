package com.revbingo.spiff.events;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import com.revbingo.spiff.instructions.IntegerInstruction;
import com.revbingo.spiff.instructions.ReferencedInstruction;

public class TestCaseClassBindingEventDispatcherGroupings {

	private ClassBindingEventDispatcher<RootBinding> unit;
	
	@Before
	public void setUp() {
		unit = new ClassBindingEventDispatcher<RootBinding>(RootBinding.class);
	}
	
	@Test
	public void startingAndEndingGroupCreatesBoundObject() {
		unit.notifyGroup("inner", true);
		unit.notifyGroup("inner", false);
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding, is(notNullValue()));
		
		assertThat(binding.inner, is(notNullValue()));
	}
	
	@Test
	public void fieldsSetOnInnerObjectWhenInGroup() {
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
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.inner, is(notNullValue()));
		assertThat(binding.inner.x, is(3));
		assertThat(binding.inner.y, is(21));
	}
	
	@Test
	public void restoresPreviousBindingAtEndOfGroup() {
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
		
		RootBinding binding = unit.getBoundValue();
		assertThat(binding, is(notNullValue()));
		assertThat(binding.inner, is(notNullValue()));
		assertThat(binding.inner.x, is(3));
		assertThat(binding.inner.y, is(21));
		assertThat(binding.x, is(30));
	}
	
	public static class RootBinding {
		public InnerBinding inner;
		public int x;
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
