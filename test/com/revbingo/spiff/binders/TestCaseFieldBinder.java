package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

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
	
	
	public static class TestClass {
		public int anInt;
		private int privateDancer;
	}
}
