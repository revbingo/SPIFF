package com.revbingo.spiff.instructions;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;

public abstract class StringInstruction extends ReferencedInstruction {

	private String encoding;
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		byte[] bytes = getBytes(buffer);
		try {
			String result = "";
			if(encoding != null){
				result = new String(bytes, encoding);
			}else{
				//use platform default
				result = new String(bytes);
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
	
	abstract byte[] getBytes(ByteBuffer buffer);
	
}
