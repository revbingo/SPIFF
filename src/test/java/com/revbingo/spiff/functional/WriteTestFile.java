/*******************************************************************************
 * Copyright 2012 Mark Piper
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
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
