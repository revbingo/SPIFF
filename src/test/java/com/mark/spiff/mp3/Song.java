package com.mark.spiff.mp3;

import java.util.List;

import com.revbingo.spiff.annotations.BindingCollection;

public class Song {

	@BindingCollection(value="StringData", type=String.class)
	public List<String> strings;

	public void printStrings() {
		for(String s : strings) {
			System.out.println(s);
		}
		System.out.println("--- END SONG --");
	}
}
