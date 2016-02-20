/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
