package com.whooa.blog.common.exception;

public abstract class BaseException extends RuntimeException {

	private int code;
	private String message;
	private String[] details;
	
	public BaseException(final int code, final String message, final String[] details) {
		this.code = code;
		this.message = message;
		this.details = details;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}

	public String[] getDetails() {
		return details;
	}
}