package com.revbingo.spiff;

public class ExecutionException extends RuntimeException {

	public ExecutionException(String msg){
		super(msg);
	}
	
	public ExecutionException(String msg, Throwable e){
		super(msg, e);
	}
}
