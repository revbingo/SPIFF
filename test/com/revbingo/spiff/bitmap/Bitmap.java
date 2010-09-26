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
	
	private List<RGBQuad> rgbQuad = new ArrayList<RGBQuad>();
	
	@BindingCollection(value="PIXELDATA", type=PixelData.class)
	private List<PixelData> pixelData;

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

	public void setPixelData(ArrayList<PixelData> pixelData) {
		this.pixelData = pixelData;
	}

}
