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
import com.revbingo.spiff.events.EventListener;
import com.revbingo.spiff.instructions.ReferencedInstruction;

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
		public void notifyData(ReferencedInstruction ins) {
			captures.add(ins.value);
		}

		@Override
		public void notifyGroup(String groupName, boolean start) {
			throw new UnsupportedOperationException();
		}

	}
}
