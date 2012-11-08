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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Test;

import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.events.ClassBindingEventListener;

public class TestMP3Parse {

	@Test
	public void parsesId3v1_1Tags() throws Exception {
		ClassBindingEventListener<Id3v1> listener = new ClassBindingEventListener<Id3v1>(Id3v1.class);
		listener.isStrict(false);

		BinaryParser parser = new BinaryParser(listener);
		parser.parse(new File("samples/id3v1.adf"), new File("test-resources/ShakingThrough.mp3"));

		Id3v1 tag = listener.getResult();

		assertThat(tag, is(notNullValue()));
		assertThat(tag.title, is("Shaking Through"));
		assertThat(tag.artist, is("R.E.M."));
		assertThat(tag.album, is("Murmur"));
		assertThat(tag.year, is("1983"));
		assertThat(tag.comment, is(""));
		assertThat(tag.trackNumber, is((byte) 10));
		assertThat(tag.genre, is("Rock"));
	}

	public static class Id3v1 {

		public byte trackNumber;
		public String genre;
		public String comment;
		public String year;
		public String album;
		public String artist;
		public String title;

		@Binding("genre")
		public void setGenre(byte genre) {
			if(genre == 17) {
				this.genre = "Rock";
			}
		}
	}
}
