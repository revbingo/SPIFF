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
package com.revbingo.spiff.bitmap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.revbingo.spiff.AdfFormatException;
import com.revbingo.spiff.BinaryParser;
import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.events.ClassBindingEventListener;

public class TestCaseBitmapAdf {

	@Test
	public void parses1BitBitmap() throws ExecutionException, AdfFormatException {
		ClassBindingEventListener<Bitmap> ed = new ClassBindingEventListener<Bitmap>(Bitmap.class);
		ed.isStrict(false);

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
		ed.isStrict(false);

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
		ed.isStrict(false);

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
		ed.isStrict(false);

		BinaryParser parser = new BinaryParser(ed);
		parser.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/24bit.bmp"));
		Bitmap bitmap = ed.getResult();

		assertThat(bitmap.getRgbQuad(), is(nullValue()));
		assertThat(bitmap.getBitmapInfoHeader().getBiHeight(), is(20));
		assertThat(bitmap.getBitmapInfoHeader().getBiWidth(), is(20));

		assertThat(bitmap.getPixelData().size(), is(400));
	}
}
