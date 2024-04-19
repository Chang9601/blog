package com.whooa.blog.comment.exception;

import com.whooa.blog.common.exception.AbstractException;

public class CommentNotFoundException extends AbstractException {
	
	public CommentNotFoundException(final int code, final String message, final String[] details) {
		super(code, message, details);
	}
}