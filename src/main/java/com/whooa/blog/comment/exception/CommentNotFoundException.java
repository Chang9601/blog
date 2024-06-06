package com.whooa.blog.comment.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class CommentNotFoundException extends CoreException {
	public CommentNotFoundException(Code code,  String[] details) {
		super(code, details);
	}
}