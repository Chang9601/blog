package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class UserNotFoundException extends AbstractException {
	public UserNotFoundException(Code exception, String[] details) {
		super(exception, details);
	}
}