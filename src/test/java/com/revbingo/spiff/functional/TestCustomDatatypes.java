/*
 * Copyright Mark Piper 2016
 *
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
 */
package com.revbingo.spiff.functional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.revbingo.spiff.parser.AdfFile;
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
			captures.add(ins.getValue());
		}

		@Override
		public void notifyGroup(String groupName, boolean start) {
			throw new UnsupportedOperationException();
		}

	}
}
