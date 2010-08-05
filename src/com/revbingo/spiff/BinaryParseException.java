package com.revbingo.spiff;

public class BinaryParseException extends Exception {

	public BinaryParseException(String msg){
		super(msg);
	}
	
	public BinaryParseException(String msg, Throwable e){
		super(msg, e);
	}
}
