package com.whooa.blog.comment.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class CommentNotBelongingToPostException extends CoreException {
	public CommentNotBelongingToPostException(Code code,  String[] details) {
		super(code, details);
	}
}