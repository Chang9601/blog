package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class UserNotFoundException extends AbstractException {
	public UserNotFoundException(Code code, String[] details) {
		super(code, details);
	}
}