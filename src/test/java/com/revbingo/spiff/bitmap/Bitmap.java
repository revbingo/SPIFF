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
