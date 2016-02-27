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
package com.revbingo.spiff.functional;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteTestFile {

	public static void main(String[] args) {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(
					new File("testfile.bin")));
			dos.writeUTF("start");
			dos.writeInt(2544);
			dos.writeFloat(3.145f);
			dos.writeByte(9);
			dos.writeLong(1010101010101010L);
			dos.writeDouble(3.145d);
			dos.writeShort(16);
			dos.writeUTF("end");
			dos.writeShort(2);
			byte[] dummy = new byte[8];
			dos.write(dummy);
			dos.writeUTF("haveme");
			dos.writeUTF("havemeagain");
			dos.writeInt(1234);
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
