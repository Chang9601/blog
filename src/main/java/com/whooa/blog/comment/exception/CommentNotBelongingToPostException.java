package com.whooa.blog.comment.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class CommentNotBelongingToPostException extends AbstractException {
	public CommentNotBelongingToPostException(Code code,  String[] details) {
		super(code, details);
	}
}