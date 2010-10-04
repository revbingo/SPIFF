package com.revbingo.spiff.instructions;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.revbingo.spiff.ExecutionException;
import com.revbingo.spiff.evaluator.Evaluator;

public class FixedLengthString extends ReferencedInstruction {

	private String lengthExpr; 
	private String encoding;
	
	@Override
	public Object evaluate(ByteBuffer buffer) throws ExecutionException {
		int length = ((Number) Evaluator.evaluate(lengthExpr)).intValue();
		byte[] bytes = new byte[length];
		buffer.get(bytes);
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
	
	public void setLengthExpr(String s){
		lengthExpr = s;
	}
	
	public String getLengthExpr() {
		return lengthExpr;
	}
	
	public void setEncoding(String enc){
		encoding = enc;
	}
	
	public String getEncoding() {
		return encoding;
	}
}
