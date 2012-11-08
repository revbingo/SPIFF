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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import com.revbingo.spiff.ExecutionException;

public class TestCaseMethodBinder {

	@Test
	public void binderCallsMethod() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("theMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		int theInt = 10;
		
		unit.bind(target, theInt);
		
		assertThat(target.privateReally, is(theInt + 1));
	}
	
	@Test(expected=ExecutionException.class)
	public void throwsExecutionExceptionIfCalledWithWrongType() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("theMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		unit.bind(target, 10f);
	}
	
	@Test(expected=ExecutionException.class)
	public void doesntCauseNullPointerIfNullPassed() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("theMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		unit.bind(target, null);
	}
	
	@Test(expected=ExecutionException.class)
	public void throwsExecutionExceptionIfMethodThrowsAnException() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("anExceptionMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		unit.bind(target, 10);
	}
	
	@Test(expected=ExecutionException.class)
	public void createAndBindCannotBeCalled() throws Exception {
		new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("theMethod", int.class)).createAndBind(null);
	}
	
	@Test
	public void privateMethodsCanBeAccessed() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("aPrivateMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		int x = 10;
		
		unit.bind(target, x);
		
		assertThat(target.privateReally, is(x));
	}
	
	@Test(expected=ExecutionException.class)
	public void exceptionThrownIfMethodIsMadeInaccessible() throws Exception {
		Method privateMethod = MethodBindingTestClass.class.getDeclaredMethod("aPrivateMethod", int.class);
		MethodBinder unit = new MethodBinder(privateMethod);
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		privateMethod.setAccessible(false);
		unit.bind(target, 10);
	}
	
	public static class MethodBindingTestClass {
		
		public int privateReally;
		
		public void theMethod(int theInt) {
			privateReally = theInt + 1;
		}
		
		public void anExceptionMethod(int theInt) {
			throw new RuntimeException("oops");
		}
		
		private void aPrivateMethod(int theInt) {
			privateReally = theInt;
		}
	}
}
