package com.whooa.blog.common.exception;

public class PostNotFoundException extends BaseException {

	public PostNotFoundException(final int code, final String message, final String[] details) {
		super(code, message, details);
	}
}