package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

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
	
	public static class TestClass {
		public int anInt;
		private int privateDancer;
		public static int statics;
		private final static int uCantTouchThis = 1;
	}
}
