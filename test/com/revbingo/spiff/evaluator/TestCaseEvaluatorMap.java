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
package com.revbingo.spiff.evaluator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

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
	
	@Test
	public void clearClearsMap() {
		EvaluatorMap unit = new EvaluatorMap();
		unit.addVariable("a", 1);
		assertThat(unit.getProperty("a", Integer.class), is(notNullValue()));
		
		unit.clear();
		assertThat(unit.getProperty("a", Integer.class), is(nullValue()));
	}
	
}
