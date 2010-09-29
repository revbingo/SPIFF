package com.revbingo.spiff.events;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.annotations.*;
import com.revbingo.spiff.binders.Binder;
import com.revbingo.spiff.binders.ObjectCollectionBinder;
import com.revbingo.spiff.binders.FieldBinder;
import com.revbingo.spiff.binders.MethodBinder;
import com.revbingo.spiff.binders.PrimitiveCollectionBinder;

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
	public void collectionFieldsForPrimitivesReturnPrimitiveCollectionBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("strings", BindingTest.class);
		assertThat(binder, instanceOf(PrimitiveCollectionBinder.class));
		
		binder = unit.getBindingFor("integers", BindingTest.class);
		assertThat(binder, instanceOf(PrimitiveCollectionBinder.class));
		
		binder = unit.getBindingFor("floats", BindingTest.class);
		assertThat(binder, instanceOf(PrimitiveCollectionBinder.class));
	}
	

	@Test
	public void collectionFieldsForNonPrimitivesReturnObjectCollectionBinder() {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("objects", BindingTest.class);
		
		assertThat(binder, instanceOf(ObjectCollectionBinder.class));
	}
	
	@Test
	public void returnsNullIfNoBindingFound() {
		BindingFactory unit = new BindingFactory();
		
		assertThat(unit.getBindingFor("nothing", BindingTest.class), is(nullValue()));
	}
	
	@Test
	public void returnsPrimitiveCollectionBinderIfEBoundToCollectionOfPrimitivesButNotDeclaredAsCollection() throws Exception {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("notExplicitlyACollection", BindingTest.class);
		
		assertThat(binder, is(instanceOf(PrimitiveCollectionBinder.class)));
	}
	
	@Test
	public void collectedTypesInferredFromGenericsIfNotExplicitInBindingAnnotation() throws Exception {
		BindingFactory unit = new BindingFactory();
		
		Binder binder = unit.getBindingFor("notTellingYouTheType", BindingTest.class);
		
		assertThat(binder, is(instanceOf(PrimitiveCollectionBinder.class)));
	}
	
	public static class BindingTest {
		
		public List<String> strings;
		public List<Integer> integers;
		
		@BindingCollection(type=Float.class)
		public List<Float> floats;
		
		@BindingCollection(type=Object.class)
		public List<Object> objects;
		
		@Binding("notExplicitlyACollection")
		public List<Integer> moreInts;
		
		@BindingCollection("notTellingYouTheType")
		public List<Integer> evenMoreInts;
		
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
