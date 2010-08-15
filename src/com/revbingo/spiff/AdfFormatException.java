package com.revbingo.spiff;

public class AdfFormatException extends Exception {

	public AdfFormatException(String msg){
		super(msg);
	}
	
	public AdfFormatException(String msg, Throwable e){
		super(msg, e);
	}
}
