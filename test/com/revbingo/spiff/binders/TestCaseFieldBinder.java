package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.lang.reflect.Field;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.ClassBindingEventDispatcher;
import com.revbingo.spiff.events.TestCaseClassBindingEventDispatcherBasicCases.ArgsRequired;

public class TestCaseFieldBinder {

	@Test
	public void valueIsSetOnField() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getField("anInt"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, 4);
		
		assertThat(target.anInt, is(4));
	}
	
	@Test
	public void privateFieldsCanBeSet() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("privateDancer"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, 4);
		
		assertThat(target.privateDancer, is(4));
	}
	
	@Test
	public void staticFieldsCanBeSet() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("statics"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, 4);
		
		assertThat(target.statics, is(4));
	}
	
	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfWrongTypePassed() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("anInt"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, 4f);
	}
	
	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfFieldCantBeAccessed() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("uCantTouchThis"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, 4);
	}
	
	@Test(expected=ExecutionException.class)
	public void avoidsNullPointerIfNullPassed() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("uCantTouchThis"));
		
		TestClass target = new TestClass();
		
		unit.bind(target, null);
	}
	
	@Test
	public void createAndBindCreatesANewInstanceAndBindsItToTheField() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("another"));
		
		TestClass target = new TestClass();
		
		Object binding = unit.createAndBind(target);
		
		assertThat(binding, instanceOf(AnotherClass.class));
		assertThat(target.another, is(sameInstance(binding)));
	}

	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfClassCannotBeInstantiatedWithNoArgs() throws Exception {
		FieldBinder unit = new FieldBinder(TestClass.class.getDeclaredField("argsRequired"));
		
		TestClass target = new TestClass();
		
		unit.createAndBind(target);
	}
	
	@Test(expected=ExecutionException.class)
	public void executionExceptionThrownIfFieldReprivatised() throws Exception {
		Field f = TestClass.class.getDeclaredField("privateConstructor");
		FieldBinder unit = new FieldBinder(f);
		
		TestClass target = new TestClass();
		
		f.setAccessible(false);
		unit.createAndBind(target);
	}
	
	public static class TestClass {
		public int anInt;
		private int privateDancer;
		public static int statics;
		private final static int uCantTouchThis = 1;
		
		public AnotherClass another;
		public ArgsRequired argsRequired;
		public PrivateConstructor privateConstructor;
	}
	
	public static class AnotherClass {
		
	}
	
	public static class ArgsRequired {
		public ArgsRequired(String s) {} 
	}
	
	public static class PrivateConstructor {
		private PrivateConstructor() {}
	}
}
