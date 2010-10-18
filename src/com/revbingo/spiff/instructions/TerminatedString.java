package com.revbingo.spiff.instructions;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class TerminatedString extends StringInstruction {

	public TerminatedString(String charsetName) {
		super(charsetName);
	}

	@Override
	public byte[] getBytes(ByteBuffer buffer) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte nextByte;
		while((nextByte = buffer.get()) != 0x00) {
			baos.write(nextByte);
		}

		return baos.toByteArray();
	}
}
