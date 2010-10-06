package com.revbingo.spiff.bitmap;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.ClassBindingEventDispatcher;

public class TestCaseBitmapAdf {

	@Test
	public void parses1BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventDispatcher<Bitmap> ed = new ClassBindingEventDispatcher<Bitmap>(Bitmap.class);
		ed.isStrict(false);
		
		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/1bit.bmp"));
		Bitmap bitmap = ed.getResult();
		
		assertThat(bitmap.getRgbQuad().size(), is(2));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));
		
		assertThat(bitmap.getPixelData().size(), is(400));
	}
}
