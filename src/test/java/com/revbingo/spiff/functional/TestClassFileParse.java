package com.revbingo.spiff.functional;

import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

public class TestClassFileParse {

	@Test
	@Ignore("Not supported by EvalEx")
	public void canParseClassFile() throws Exception {
		EventListener noopListener = new EventListener(){

			@Override public void notifyData(Datatype ins) {}
			@Override public void notifyGroup(String groupName, boolean start) {}
		};

		BinaryParser parser = new BinaryParser(noopListener);
		parser.parse(new File("test-resources/javaclass.adf"), new File("target/test-classes/com/revbingo/spiff/functional/TestClassFileParse.class"));
	}
}
