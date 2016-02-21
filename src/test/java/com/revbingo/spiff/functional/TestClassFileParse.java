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

import java.io.File;

import com.revbingo.spiff.datatypes.Datatype;
import com.revbingo.spiff.events.EventListener;
import org.junit.Test;

import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.events.DebugEventListener;

public class TestClassFileParse {

	@Test
	public void canParseClassFile() throws Exception {
		EventListener noopListener = new EventListener(){

			@Override public void notifyData(Datatype ins) {}
			@Override public void notifyGroup(String groupName, boolean start) {}
		};

		BinaryParser parser = new BinaryParser(noopListener);
		parser.parse(new File("test-resources/javaclass.adf"), new File("target/test-classes/com/revbingo/spiff/functional/TestClassFileParse.class"));
	}
}
