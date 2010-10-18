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
