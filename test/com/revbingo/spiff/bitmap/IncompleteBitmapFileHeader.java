package com.revbingo.spiff.bitmap;

public class IncompleteBitmapFileHeader {

	private short bfType;
	private int bfOffBits;
	
	public void setBfType(short bfType) {
		this.bfType = bfType;
	}
	public short getBfType() {
		return bfType;
	}
	public void setBfOffBits(int bfOffBits) {
		this.bfOffBits = bfOffBits;
	}
	public int getBfOffBits() {
		return bfOffBits;
	}
}
