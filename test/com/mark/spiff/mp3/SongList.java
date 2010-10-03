package com.mark.spiff.mp3;

import java.util.List;

import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;

public class SongList {
	
	@BindingCollection(value="TrackListItem", type=Song.class)
	public List<Song> songs;
	
	public void printSongs() {
		System.out.println("!!!" + songs.size());
		for(Song s: songs) {
			s.printStrings();
		}
	}
}
