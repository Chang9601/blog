package com.whooa.blog.post.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class PostNotFoundException extends AbstractException {

	public PostNotFoundException(Code exception, String[] details) {
		super(exception, details);
	}
}