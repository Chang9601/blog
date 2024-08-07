package com.whooa.blog.post.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class PostNotFoundException extends CoreException {
	private static final long serialVersionUID = 1L;

	public PostNotFoundException(Code code, String[] details) {
		super(code, details);
	}
}