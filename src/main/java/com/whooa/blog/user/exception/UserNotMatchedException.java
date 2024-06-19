package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class UserNotMatchedException extends AbstractException {
	public UserNotMatchedException(Code code, String[] detail) {
		super(code, detail);
	}
}