package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class UserNotMatchedException extends CoreException {
	public UserNotMatchedException(Code code, String[] detail) {
		super(code, detail);
	}
}