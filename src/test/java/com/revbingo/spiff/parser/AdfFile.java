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
