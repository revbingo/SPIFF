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
