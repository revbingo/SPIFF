package com.revbingo.spiff.test;

import java.io.File;

import org.junit.Test;

import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.events.DebugEventListener;

public class TestClassFileParse {

	@Test
	public void canParseClassFile() throws Exception {
		DebugEventListener listener = new DebugEventListener();

		BinaryParser parser = new BinaryParser(listener);
		parser.parse(new File("test-resources/javaclass.adf"), new File("bin-test/com/revbingo/spiff/test/TestClassFileParse.class"));


	}
}
