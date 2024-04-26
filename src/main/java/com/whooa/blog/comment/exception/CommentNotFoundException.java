package com.whooa.blog.comment.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class CommentNotFoundException extends AbstractException {
	
	public CommentNotFoundException(Code exception,  String[] details) {
		super(exception, details);
	}
}