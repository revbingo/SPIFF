package com.revbingo.spiff.bitmap;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.ClassBindingEventListener;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TestCaseBitmapAdf {

	@Test
	public void parses1BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventListener<Bitmap> ed = new ClassBindingEventListener<Bitmap>(Bitmap.class);
		ed.setStrict(false);

		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/1bit.bmp"));
		Bitmap bitmap = ed.getResult();

		assertThat(bitmap.getRgbQuad().size(), is(2));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));

		assertThat(bitmap.getPixelData().size(), is(400));
	}

	@Test
	@Ignore
	public void parses4BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventListener<Bitmap> ed = new ClassBindingEventListener<Bitmap>(Bitmap.class);
		ed.setStrict(false);

		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/4bit.bmp"));
		Bitmap bitmap = ed.getResult();

		assertThat(bitmap.getRgbQuad().size(), is(16));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));

		assertThat(bitmap.getPixelData().size(), is(400));
	}

	@Test
	public void parses8BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventListener<Bitmap> ed = new ClassBindingEventListener<Bitmap>(Bitmap.class);
		ed.setStrict(false);

		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/8bit.bmp"));
		Bitmap bitmap = ed.getResult();

		assertThat(bitmap.getRgbQuad().size(), is(256));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));

		assertThat(bitmap.getPixelData().size(), is(400));
	}

	@Test
	@Ignore
	public void parses24BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventListener<Bitmap> ed = new ClassBindingEventListener<Bitmap>(Bitmap.class);
		ed.setStrict(false);

		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/24bit.bmp"));
		Bitmap bitmap = ed.getResult();

		assertThat(bitmap.getRgbQuad(), is(nullValue()));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));

		assertThat(bitmap.getPixelData().size(), is(400));
	}
}
