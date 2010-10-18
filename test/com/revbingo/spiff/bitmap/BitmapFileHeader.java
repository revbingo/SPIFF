/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.bitmap;

import com.revbingo.spiff.annotations.Binding;

public class BitmapFileHeader {

	@Binding("bfType")
	private String bfTypeWithAnotherName;
	private int bfSize;
	private short bfReserved1;
	private short bfReserved2;
	private int bfOffBits;
	
	public String getBfTypeWithAnotherName() {
		return bfTypeWithAnotherName;
	}
	public void setBfTypeWithAnotherName(String bfType) {
		this.bfTypeWithAnotherName = bfType;
	}
	
	public int getBfSize() {
		return bfSize;
	}
	public void setBfSize(int bfSize) {
		this.bfSize = bfSize;
	}
	public short getBfReserved1() {
		return bfReserved1;
	}
	public void setBfReserved1(short bfReserved1) {
		this.bfReserved1 = bfReserved1;
	}
	public short getBfReserved2() {
		return bfReserved2;
	}
	public void setBfReserved2(short bfReserved2) {
		this.bfReserved2 = bfReserved2;
	}
	public int getBfOffBits() {
		return bfOffBits;
	}
	public void setBfOffBits(int bfOffBits) {
		this.bfOffBits = bfOffBits;
	}
	
}
