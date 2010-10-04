package com.revbingo.spiff.instructions;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class TerminatedString extends StringInstruction {

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
