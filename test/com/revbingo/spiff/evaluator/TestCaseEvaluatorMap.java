package com.revbingo.spiff.evaluator;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.revbingo.spiff.evaluator.EvaluatorMap;

@RunWith(Parameterized.class)
public class TestCaseEvaluatorMap {

	@Parameters
	public static Collection<Object[]> generateData() {
		return Arrays.asList(new Object[][] {
				{ 1f, Float.class },
				{ 1L, Long.class},
				{ (byte) 1, Byte.class},
				{ (short) 1, Short.class},
				{ 1d, Double.class},
				{ 1, Integer.class},
				{ "1", String.class },
				{ null, null},
				{ true, Boolean.class}
		});
	}

	Object value;
	Class<?> type;
	
	public TestCaseEvaluatorMap(Object value, Class<?> type) {
		this.value = value;
		this.type = type;
	}
	
	@Test
	public void getPropertyReturnsCorrectType() {
		EvaluatorMap unit = new EvaluatorMap();
		unit.addVariable("testVar", value);
		Object o = unit.getProperty("testVar", type);
		
		assertThat(o, equalTo(value));
		if(type != null) assertThat(o, instanceOf(type));
	}
	
}
