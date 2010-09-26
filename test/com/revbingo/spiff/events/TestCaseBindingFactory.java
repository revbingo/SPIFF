package com.revbingo.spiff.events;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.annotations.*;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.binders.CollectionBinder;
import com.revbingo.spiff.binders.FieldBinder;
import com.revbingo.spiff.binders.MethodBinder;

public class TestCaseBindingFactory {

	@Test
	public void boundMethodTakesPrecedenceOverBoundFieldAndReturnsMethodBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("theBoundInt", BindingTest.class);
		
		assertThat(binder, instanceOf(MethodBinder.class));
	}
	
	@Test
	public void boundFieldTakesPrecedenceOverSetterAndReturnsFieldBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("aBoundIntWithASetter", BindingTest.class);
		
		assertThat(binder, instanceOf(FieldBinder.class));
	}
	
	@Test
	public void setterTakesPrecedenceOverFieldAndReturnsMethodBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("intWithASetter", BindingTest.class);
		
		assertThat(binder, instanceOf(MethodBinder.class));
	}
	
	@Test
	public void fieldReturnsFieldBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("publicFieldInt", BindingTest.class);
		
		assertThat(binder, instanceOf(FieldBinder.class));
	}
	
	@Test
	public void collectionFieldsReturnCollectionBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("strings", BindingTest.class);
		
		assertThat(binder, instanceOf(CollectionBinder.class));
	}
	
	@Test
	public void returnsNullIfNoBindingFound() {
		BindingFactory unit = new BindingFactory();
		
		assertThat(unit.getBindingFor("nothing", BindingTest.class), is(nullValue()));
	}
	
	public static class BindingTest {
		
		public List<String> strings;
		
		public int publicFieldInt;
		
		@Binding("theBoundInt")
		public int a;
		
		@Binding("aBoundIntWithASetter")
		public int intWithASetter;
		
		@Binding("theBoundInt")
		public void setADifferentInt(int theInt) {
		}
		
		public void setIntWithASetter(int theInt) {
			
		}
	}
}
