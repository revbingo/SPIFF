/*******************************************************************************
 * Copyright (c) 2010 Mark Piper.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package com.revbingo.spiff.test;

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
