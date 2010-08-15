package com.revbingo.spiff.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import com.revbingo.spiff.util.MethodDispatcher;

public class TestCaseMethodDispatcher {

	private enum InvokedWith {
		INT_OBJECT,
		SHORT_PRIMITIVE,
		FLOAT_OBJECT,
		FLOAT_PRIMITIVE
	}
	
	@Test
	public void testGetClasses() throws Exception {
		Object[] objects = new Object[] { "1233", 1, 3L, 9.3d };
		
		Class<?>[] result = MethodDispatcher.getClasses(objects);
		
		assertEquals(String.class, result[0]);
		assertEquals(Integer.class, result[1]);
		assertEquals(Long.class, result[2]);
		assertEquals(Double.class, result[3]);
	}
	
	@Test
	public void testDispatch() throws Exception {
		TestReceiver receiver = new TestReceiver();

		InvokedWith result = (InvokedWith) MethodDispatcher.dispatch("testMethod", receiver, 30);
		assertThat(result, equalTo(InvokedWith.INT_OBJECT));
		
		result = (InvokedWith) MethodDispatcher.dispatch("testMethod", receiver, (short) 30);
		assertThat(result, equalTo(InvokedWith.SHORT_PRIMITIVE));
		
		result = (InvokedWith) MethodDispatcher.dispatch("testMethod", receiver, new Float(1.0));
		assertThat(result, equalTo(InvokedWith.FLOAT_OBJECT));

		//Known issue - passing an Object[] means that an overloaded method will always be invoked on
		//the boxed type, not the primitive
//		result = (InvokedWith) MethodDispatcher.dispatch("testMethod", receiver, 1.0f);
//		assertThat(result, equalTo(InvokedWith.FLOAT_PRIMITIVE));
		
	}
	
	@Test
	public void alternativeTypesForPrimitivesReturnsBoxedEquivalents() {
		Class<?>[] alternatives = MethodDispatcher.getAlternativeTypes(byte.class, int.class, short.class, long.class, double.class, float.class, boolean.class);
		
		assertThat(alternatives.length, is(7));
		
		assertArrayEquals(new Class[] {Byte.class, Integer.class, Short.class, Long.class, Double.class, Float.class, Boolean.class}, alternatives);
	}
	
	@Test
	public void alternativeTypesForBoxedTypesReturnsPrimitiveEquivalents() {
		Class<?>[] alternatives = MethodDispatcher.getAlternativeTypes(Byte.class, Integer.class, Short.class, Long.class, Double.class, Float.class, Boolean.class);
		
		assertThat(alternatives.length, is(7));
		
		assertArrayEquals(new Class[] {byte.class, int.class, short.class, long.class, double.class, float.class, boolean.class}, alternatives);
	}
	
	@Test
	@SuppressWarnings(value="unchecked")
	public void getAlternativeTypesReturnsTheSameTypeForUnknownTypes() {
		Class[] alternatives = MethodDispatcher.getAlternativeTypes(TestReceiver.class);
		
		assertThat(alternatives.length, is(1));
		assertThat(alternatives[0], is(equalTo(TestReceiver.class)));
	}
	
	
	public class TestReceiver {
		
		public InvokedWith testMethod(Integer i) {
			return InvokedWith.INT_OBJECT;
		}
		
		public InvokedWith testMethod(short s) {
			return InvokedWith.SHORT_PRIMITIVE;
		}
		
		public InvokedWith testMethod(Float f) {
			return InvokedWith.FLOAT_OBJECT;
		}
		
//		public InvokedWith testMethod(float f) {
//			return InvokedWith.FLOAT_PRIMITIVE;
//		}
	}
}
