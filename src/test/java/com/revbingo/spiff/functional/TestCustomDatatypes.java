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
package com.revbingo.spiff.functional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;

public class TestCustomDatatypes {

	@Test
	public void testCustomDatatype() throws ExecutionException, AdfFormatException {
		CapturingEventListener ed = new CapturingEventListener();

		BinaryParser unit = new BinaryParser(ed);

		unit.parse(new File("test-resources/custom-datatype.adf"), new File("test-resources/iTunesDB"));

		assertThat(ed.captures.size(), is(1));
		assertThat(ed.captures.get(0), instanceOf(String.class));
		assertThat((String) ed.captures.get(0), is("dbhm"));
	}

	public static class CapturingEventListener implements EventListener {

		public List<Object> captures = new ArrayList<Object>();

		@Override
		public void notifyData(Datatype ins) {
			captures.add(ins.value);
		}

		@Override
		public void notifyGroup(String groupName, boolean start) {
			throw new UnsupportedOperationException();
		}

	}
}
