package com.whooa.blog.common.exception;

public class PostNotFoundException extends BaseException {
    private static final long serialVersionUID = 1L;

	public PostNotFoundException(final int code, final String message, final String[] details) {
		super(code, message, details);
	}
}