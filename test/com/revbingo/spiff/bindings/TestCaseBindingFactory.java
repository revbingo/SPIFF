package com.revbingo.spiff.bindings;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

public class TestCaseBindingFactory {

	Mockery context;
	BindingWrapper wrapper;
	
	@Before
	public void createMockery() {
		context = new Mockery();
		context.setImposteriser(ClassImposteriser.INSTANCE);
		wrapper = context.mock(BindingWrapper.class);
	}
	
	@Test
	public void getBindingsReturnsAllBindingsAddedToTheFactory() {
		BindingFactory unit = new BindingFactory();
		
		unit.put("one", wrapper);
		unit.put("two", wrapper);
		unit.put("three", wrapper);
		
		assertThat(unit.getBindings().size(), is(3));
	}
	
	@Test
	public void getMethodRequestsCopyOfTheBindingNotTheOriginal() {
		final BindingWrapper returnedWrapper = context.mock(BindingWrapper.class, "anotherBindingWrapper");
		
		context.checking(new Expectations(){{
			allowing(wrapper).createFromTemplate();
				will(returnValue(returnedWrapper));
		}});
		
		BindingFactory unit = new BindingFactory();
		
		unit.put("one", wrapper);
		
		assertThat(unit.get("one"), is(returnedWrapper));
		assertThat(unit.get("one"), is(not(wrapper)));
		
		context.assertIsSatisfied();
	}
	
	@Test
	public void bindsMethodReturnsTrueIfBindingHasBeenAdded_AndFalseIfNot() {
		BindingFactory unit = new BindingFactory();
		
		unit.put("one", wrapper);
		
		assertThat(unit.binds("one"), is(true));
		assertThat(unit.binds("two"), is(false));
	}
	
	@Test
	public void setBindingsOverridesAllPreviousBindings() {
		BindingFactory unit = new BindingFactory();	
		
		unit.put("one", wrapper);
		
		Map<String, BindingWrapper> newMap = new HashMap<String, BindingWrapper>();
		
		newMap.put("two", wrapper);
		newMap.put("three", wrapper);
		
		unit.setBindings(newMap);
		
		assertThat(unit.getBindings().size(), is(2));
		assertThat(unit.binds("one"), is(false));
		assertThat(unit.binds("two"), is(true));
		assertThat(unit.binds("three"), is(true));
		
		
	}
}
