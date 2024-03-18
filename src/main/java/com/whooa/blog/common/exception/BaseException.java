package com.whooa.blog.common.exception;

public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	private int code;
	private String message;
	
	public BaseException(final int code, final String message) {
		this.code = code;
		this.message = message;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
}