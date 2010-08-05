package com.mark.spiff.mp3;

import java.util.List;

import com.revbingo.spiff.annotations.BindingCollection;

public class Song {

	@BindingCollection(value="mhodString", type=String.class)
	public List<String> strings;

	public void printStrings() {
		System.out.println("Song " + strings.size());
		for(String s : strings) {
			System.out.println(s + " / ");
		}
		System.out.println("--- END SONG --");
	}
}
