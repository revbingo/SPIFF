/*******************************************************************************
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
 ******************************************************************************/
package com.revbingo.spiff.bitmap;

public class BitmapInfoHeader {

	private int biSize;
	private int biWidth;
	private int biHeight;
	private short biPlanes;
	private short	biBitCount;
	private int	biCompression;
	private int	biSizeImage;
	private int	biXPelsPerMeter;
	private int	biYPelsPerMeter;
	private int	biClrUsed;
	private int	biClrImportant;
	
	public int getBiSize() {
		return biSize;
	}
	public void setBiSize(int biSize) {
		this.biSize = biSize;
	}
	public int getBiWidth() {
		return biWidth;
	}
	public void setBiWidth(int biWidth) {
		this.biWidth = biWidth;
	}
	public int getBiHeight() {
		return biHeight;
	}
	public void setBiHeight(int biHeight) {
		this.biHeight = biHeight;
	}
	public short getBiPlanes() {
		return biPlanes;
	}
	public void setBiPlanes(short biPlanes) {
		this.biPlanes = biPlanes;
	}
	public short getBiBitCount() {
		return biBitCount;
	}
	public void setBiBitCount(short biBitCount) {
		this.biBitCount = biBitCount;
	}
	public int getBiCompression() {
		return biCompression;
	}
	public void setBiCompression(int biCompression) {
		this.biCompression = biCompression;
	}
	public int getBiSizeImage() {
		return biSizeImage;
	}
	public void setBiSizeImage(int biSizeImage) {
		this.biSizeImage = biSizeImage;
	}
	public int getBiXPelsPerMeter() {
		return biXPelsPerMeter;
	}
	public void setBiXPelsPerMeter(int biXPelsPerMeter) {
		this.biXPelsPerMeter = biXPelsPerMeter;
	}
	public int getBiYPelsPerMeter() {
		return biYPelsPerMeter;
	}
	public void setBiYPelsPerMeter(int biYPelsPerMeter) {
		this.biYPelsPerMeter = biYPelsPerMeter;
	}
	public int getBiClrUsed() {
		return biClrUsed;
	}
	public void setBiClrUsed(int biClrUsed) {
		this.biClrUsed = biClrUsed;
	}
	public int getBiClrImportant() {
		return biClrImportant;
	}
	public void setBiClrImportant(int biClrImportant) {
		this.biClrImportant = biClrImportant;
	}
}
