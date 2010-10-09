package com.revbingo.spiff;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.revbingo.spiff.bitmap.Bitmap;
import com.revbingo.spiff.bitmap.BitmapFileHeader;
import com.revbingo.spiff.bitmap.BitmapInfoHeader;
import com.revbingo.spiff.bitmap.IncompleteBitmap;
import com.revbingo.spiff.bitmap.IncompleteBitmapFileHeader;
import com.revbingo.spiff.bitmap.PixelData;
import com.revbingo.spiff.events.ClassBindingEventListener;

public class TestCaseFunctionalPopulateClass {

	@Test
	public void testPopulateBeans() throws Exception {
		ClassBindingEventListener<Bitmap> eventDispatcher = new ClassBindingEventListener<Bitmap>(Bitmap.class);  //BindingEventDispatcher.getInstance(Bitmap.class);
		BinaryParser unit = new BinaryParser(eventDispatcher);
		
		unit.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/mono.bmp"));
		
		Bitmap bitmap = eventDispatcher.getResult();
		
		BitmapFileHeader fileHeader;
		BitmapInfoHeader infoHeader;
		assertThat((fileHeader = bitmap.getBitmapFileHeader()), is(notNullValue()));
		assertThat((infoHeader = bitmap.getBitmapInfoHeader()), is(notNullValue()));
		
		assertThat(fileHeader.getBfTypeWithAnotherName(), equalTo("BM"));
		assertThat(fileHeader.getBfSize(), equalTo(134));
		assertThat(fileHeader.getBfReserved1(), equalTo((short) 0));
		assertThat(fileHeader.getBfReserved2(), equalTo((short) 0));
		assertThat(fileHeader.getBfOffBits(), equalTo(0x36));
		
		assertThat(infoHeader.getBiSize(), equalTo(40));
		assertThat(infoHeader.getBiWidth(), equalTo(5));
		assertThat(infoHeader.getBiHeight(), equalTo(5));
		assertThat(infoHeader.getBiPlanes(), equalTo((short) 1));
		assertThat(infoHeader.getBiBitCount(), equalTo((short) 24));
		assertThat(infoHeader.getBiCompression(), equalTo(0));
		assertThat(infoHeader.getBiSizeImage(), equalTo(80));
		assertThat(infoHeader.getBiXPelsPerMeter(), equalTo(0));
		assertThat(infoHeader.getBiYPelsPerMeter(), equalTo(0));
		assertThat(infoHeader.getBiClrUsed(), equalTo(0));
		assertThat(infoHeader.getBiClrImportant(), equalTo(0));
		
		List<PixelData> pixelData;
		assertThat((pixelData = bitmap.getPixelData()), is(notNullValue()));
		
		List<short[]> expectedValues =  Arrays.asList(new short[][] { {0xFE, 0xFE, 0xFE}, {0x00, 0x80, 0x80}, {0xFE, 0xFE, 0xFE},
				{0x00, 0x80, 0x80}, {0xFE, 0xFE, 0xFE}, {0x40, 0x80, 0xFF}, {0x00, 0x00, 0x00}, {0XFF, 0x00, 0xFF},
				{0xFF, 0x00, 0xFF}, {0x00, 0x00, 0x00}, {0x40, 0x80, 0xFF}, {0x00, 0x00, 0x00}, {0x00, 0xFF, 0xFF},
				{0xFE, 0xFE, 0xFE}, {0x00, 0xFF, 0xFF}, {0xFE, 0xFE, 0xFE}, {0x00, 0x00, 0x80}, {0x00, 0xFF, 0xFF},
				{0xFF, 0x00, 0xFF}, {0x00, 0xFF, 0xFF}, {0x00, 0x00, 0xFF}, {0xFF, 0x00, 0x80}, {0xFE, 0xFE, 0xFE}, 
				{0x00, 0xFF, 0xFF}, {0x00, 0xFF, 0xFF} });
		
		int i = 0;
		for(PixelData pd : pixelData) {
			short[] actualValues = new short[] { pd.rgbBlue, pd.rgbGreen, pd.rgbRed };
			short[] expectedPixels = expectedValues.get(i++);
			assertArrayEquals(expectedPixels, actualValues);
		}
	}
	
	@Test
	public void testNonStrictMode() throws Exception {
		ClassBindingEventListener<IncompleteBitmap> eventDispatcher = new ClassBindingEventListener<IncompleteBitmap>(IncompleteBitmap.class);
		eventDispatcher.isStrict(false);
		
		BinaryParser unit = new BinaryParser(eventDispatcher);
		
		unit.parse(new File("test-resources/bitmap_class.adf"), new File("test-resources/mono.bmp"));
		
		IncompleteBitmap bitmap = (IncompleteBitmap) eventDispatcher.getResult();
		
		IncompleteBitmapFileHeader fileHeader;
		
		assertThat((fileHeader = bitmap.getFileHeader()), is(notNullValue()));
		
		assertThat(fileHeader.getBfType(), is("BM"));
		assertThat(fileHeader.getBfOffBits(), equalTo(0x36));
	}
	
	@Test(expected=ExecutionException.class)
	public void testThrowsExceptionInStrictMode() throws Exception {
		ClassBindingEventListener<IncompleteBitmap> eventDispatcher = new ClassBindingEventListener<IncompleteBitmap>(IncompleteBitmap.class);
		eventDispatcher.isStrict(true);
		
		BinaryParser unit = new BinaryParser(eventDispatcher);
		
		unit.parse(new File("test-resources/bitmap_class.adf"), new File("mono.bmp"));
	}
}
