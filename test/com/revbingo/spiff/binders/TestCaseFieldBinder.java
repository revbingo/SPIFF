/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.revbingo.spiff.binders;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

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
