/*
 * Copyright Mark Piper 2016
 *
 * This file is part of SPIFF.
 *
 * SPIFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPIFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPIFF.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.revbingo.spiff.functional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeModel;

import org.junit.Test;

import com.mark.spiff.mp3.SongList;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.TreeBuildingEventListener;
import com.revbingo.spiff.events.ClassBindingEventListener;

public class TestITunesParse {

	@Test
	public void testITunesParse() throws Exception {
		ClassBindingEventListener<SongList> ed = new ClassBindingEventListener<SongList>(SongList.class);
		BinaryParser parser = new BinaryParser(ed);
		ed.isStrict(false);
		parser.parse(new File("samples/itunesdb.adf"), new File("test-resources/iTunesDB"));

		SongList result = ed.getResult();

		assertThat(result.songs.size(), is(5375));

		assertThat(result.songs.get(0).strings.get(0), is("Glenn Miller-In The Mood"));
		assertThat(result.songs.get(0).strings.get(1), is(":iPod_Control:Music:F23:RPDM.mp3"));
		assertThat(result.songs.get(0).strings.get(2), is("Best of Big Band Swing"));
	}

	public void testITunesParseTree() throws Exception {
		TreeBuildingEventListener ed = new TreeBuildingEventListener();
		BinaryParser parser = new BinaryParser(ed);

		parser.parse(new File("itunesdb.adf"), new File("iTunesDB"));

		TreeModel model = ed.getTree();
		JFrame frame = new JFrame("Tree");

		JScrollPane scroll = new JScrollPane();
		JTree tree = new JTree(model);
		scroll.setSize(new Dimension(600,600));
		scroll.setBorder(BorderFactory.createLineBorder(Color.RED));
		scroll.getViewport().add(tree);
		frame.getContentPane().add(scroll);
		frame.setSize(new Dimension(600,600));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) throws Exception {
		try {
		    // Set cross-platform Java L&F (also called "Metal")
	        UIManager.setLookAndFeel(
	           "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
	    }
	    catch (Exception e) {
	       // handle exception
	    }
		new TestITunesParse().testITunesParseTree();
	}

}
