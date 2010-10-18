/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
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
