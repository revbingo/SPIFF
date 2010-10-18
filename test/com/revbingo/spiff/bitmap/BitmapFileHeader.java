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
