package com.revbingo.spiff.binders;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

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
	
	@Test
	public void privateMethodsCanBeAccessed() throws Exception {
		MethodBinder unit = new MethodBinder(MethodBindingTestClass.class.getDeclaredMethod("aPrivateMethod", int.class));
		
		MethodBindingTestClass target = new MethodBindingTestClass();
		
		int x = 10;
		
		unit.bind(target, x);
		
		assertThat(target.privateReally, is(x));
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
