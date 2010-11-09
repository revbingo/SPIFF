package com.revbingo.spiff.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class AdfFile {

	private StringBuffer buffer = new StringBuffer();

	public static AdfFile start() {
		return new AdfFile();
	}

	public AdfFile add(String line) {
		return add(line, true);
	}

	public AdfFile add(String line, boolean cr) {
		buffer.append(line);
		if(cr) buffer.append("\n");
		return this;
	}

	public AdfFile end() {
		return this;
	}

	public InputStream asInputStream() {
		try {
			return new ByteArrayInputStream(buffer.toString().getBytes("US-ASCII"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}