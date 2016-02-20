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
