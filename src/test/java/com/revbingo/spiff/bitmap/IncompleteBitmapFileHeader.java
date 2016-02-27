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

public class IncompleteBitmapFileHeader {

	private String bfType;
	private int bfOffBits;
	
	public void setBfType(String bfType) {
		this.bfType = bfType;
	}
	public String getBfType() {
		return bfType;
	}
	public void setBfOffBits(int bfOffBits) {
		this.bfOffBits = bfOffBits;
	}
	public int getBfOffBits() {
		return bfOffBits;
	}
}
