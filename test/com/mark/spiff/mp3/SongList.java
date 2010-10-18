/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.mark.spiff.mp3;

import java.util.List;

import com.revbingo.spiff.annotations.BindingCollection;

public class SongList {
	
	@BindingCollection(value="TrackListItem", type=Song.class)
	public List<Song> songs;
	
	public void printSongs() {
		for(Song s: songs) {
			s.printStrings();
		}
	}
}
