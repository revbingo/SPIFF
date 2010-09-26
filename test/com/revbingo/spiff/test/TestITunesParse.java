package com.revbingo.spiff.test;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeModel;

import org.junit.Ignore;
import org.junit.Test;

import com.mark.spiff.mp3.SongList;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.TreeBuildingEventDispatcher;
import com.revbingo.spiff.events.ClassBindingEventDispatcher;

public class TestITunesParse {

	@Ignore
	@Test
	public void testITunesParse() throws Exception {
		ClassBindingEventDispatcher<SongList> ed = new ClassBindingEventDispatcher<SongList>(SongList.class);
		BinaryParser parser = new BinaryParser(ed);
		
		parser.parse(new File("itunesdb.adf"), new File("iTunesDB"));
		
		SongList result = ed.getResult();
		System.out.println(result.songs.size());
		result.printSongs();
	}
	
	
	public void testITunesParseTree() throws Exception {
		TreeBuildingEventDispatcher ed = new TreeBuildingEventDispatcher();
		BinaryParser parser = new BinaryParser(ed);
		
		parser.parse(new File("itunesdb.adf"), new File("iTunesDB"));
		//parser.parse(new File("test-resources/bitmap_class.adf"), new File("mono.bmp"));
		
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
