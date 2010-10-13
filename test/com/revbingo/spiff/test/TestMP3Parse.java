package com.revbingo.spiff.test;

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
	public void parsesId3Tags() throws Exception {
		ClassBindingEventListener<Id3v1> listener = new ClassBindingEventListener<Id3v1>(Id3v1.class);
		listener.isStrict(false);

		BinaryParser parser = new BinaryParser(listener);
		parser.parse(new File("samples/id3v1.adf"), new File("test-resources/ShakingThrough.mp3"));

		Id3v1 tag = listener.getResult();

		assertThat(tag, is(notNullValue()));
		assertThat(tag.title.trim(), is("Shaking Through"));
		assertThat(tag.artist.trim(), is("R.E.M."));
		assertThat(tag.album.trim(), is("Murmur"));
		assertThat(tag.year, is("1983"));
		assertThat(tag.comment.trim(), is(""));
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
