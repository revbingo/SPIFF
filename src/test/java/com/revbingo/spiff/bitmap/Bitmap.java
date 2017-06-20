package com.revbingo.spiff.bitmap;

import java.util.ArrayList;
import java.util.List;

import com.revbingo.spiff.annotations.Binding;
import com.revbingo.spiff.annotations.BindingCollection;

public class Bitmap {

	@Binding("BITMAPFILEHEADER")
	private BitmapFileHeader bitmapFileHeader;

	@Binding("BITMAPINFOHEADER")
	private BitmapInfoHeader bitmapInfoHeader;

	@Binding("RGBQUAD")
	private List<RGBQuad> rgbQuad = new ArrayList<RGBQuad>();

	@BindingCollection(value="pixelData", type=PixelData.class)
	private List<PixelData> pixelData = new ArrayList<PixelData>();

	public BitmapFileHeader getBitmapFileHeader() {
		return bitmapFileHeader;
	}

	public void setBitmapFileHeader(BitmapFileHeader fileHeader) {
		this.bitmapFileHeader = fileHeader;
	}

	public BitmapInfoHeader getBitmapInfoHeader() {
		return bitmapInfoHeader;
	}

	public void setBitmapInfoHeader(BitmapInfoHeader infoHeader) {
		this.bitmapInfoHeader = infoHeader;
	}

	public List<RGBQuad> getRgbQuad() {
		return rgbQuad;
	}

	public void setRgbQuad(List<RGBQuad> rgbQuad) {
		this.rgbQuad = rgbQuad;
	}

	public List<PixelData> getPixelData() {
		return pixelData;
	}

	@Binding("pixelBits")
	public void convert1BitPixelData(boolean[] bits) {
		for(int i = 0; i < bits.length; i++) {
			mapPixelFromColourTable(bits[i] ? 1 : 0);
		}
	}

	@Binding("pixel8")
	public void convert8BitPixelData(byte b) {
		mapPixelFromColourTable(b);
	}

	private void mapPixelFromColourTable(int quadIndex) {
		RGBQuad quad = rgbQuad.get(quadIndex);
		PixelData d = new PixelData();
		d.rgbBlue = quad.rgbBlue;
		d.rgbRed = quad.rgbRed;
		d.rgbGreen = quad.rgbGreen;

		pixelData.add(d);
	}
}
