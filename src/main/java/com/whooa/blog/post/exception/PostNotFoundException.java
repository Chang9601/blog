package com.whooa.blog.post.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class PostNotFoundException extends CoreException {
	public PostNotFoundException(Code code, String[] details) {
		super(code, details);
	}
}