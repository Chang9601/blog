package com.whooa.blog.common.exception;


import com.whooa.blog.common.code.Code;

public abstract class AbstractException extends RuntimeException {

	private Code exception;
	private String[] details;
	
	public AbstractException(Code exception,  String[] details) {
		this.exception = exception;
		this.details = details;
	}
	
	public Code getException() {
		return exception;
	}

	public String[] getDetails() {
		return details;
	}
}