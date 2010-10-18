/*******************************************************************************
 * This file is part of SPIFF.
 * 
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
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
