package com.whooa.blog.comment.exception;

import com.whooa.blog.common.exception.AbstractException;

public class CommentNotBelongingToPostException extends AbstractException {

	public CommentNotBelongingToPostException(final int code, final String message, final String[] details) {
		super(code, message, details);
	}
}