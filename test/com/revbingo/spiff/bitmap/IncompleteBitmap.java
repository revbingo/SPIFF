package com.revbingo.spiff.bitmap;

import com.revbingo.spiff.annotations.Binding;

@Binding("BITMAPFILE")
public class IncompleteBitmap {

	@Binding("BITMAPFILEHEADER")
	private IncompleteBitmapFileHeader fileHeader;

	public void setFileHeader(IncompleteBitmapFileHeader fileHeader) {
		this.fileHeader = fileHeader;
	}

	public IncompleteBitmapFileHeader getFileHeader() {
		return fileHeader;
	}

	
}
