package com.whooa.blog.post.exception;

import com.whooa.blog.common.exception.AbstractException;

public class PostNotFoundException extends AbstractException {

	public PostNotFoundException(final int code, final String message, final String[] details) {
		super(code, message, details);
	}
}