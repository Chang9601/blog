package com.whooa.blog.comment.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class CommentNotBelongingToPostException extends CoreException {
	private static final long serialVersionUID = 1L;

	public CommentNotBelongingToPostException(Code code,  String[] details) {
		super(code, details);
	}
}