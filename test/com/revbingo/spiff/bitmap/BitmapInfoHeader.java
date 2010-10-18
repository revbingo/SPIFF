/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
