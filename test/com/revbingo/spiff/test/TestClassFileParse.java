/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
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
