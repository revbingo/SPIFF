package com.revbingo.spiff.instructions;

import java.io.ByteArrayOutputStream;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public class TerminatedString extends ReferencedInstruction {

	private String encoding;
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		address = buffer.position();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte nextByte;
		while((nextByte = buffer.get()) != 0x00) {
			baos.write(nextByte);
		}
		
		try {
			String result = "";
			if(encoding != null){
				result = new String(baos.toByteArray(), encoding);
			}else{
				//use platform default
				result = new String(baos.toByteArray());
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			throw new ExecutionException(name + ": Unknown encoding " + encoding, e);
		}
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return encoding;
	}
}
