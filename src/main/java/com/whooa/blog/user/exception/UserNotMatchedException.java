package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class UserNotMatchedException extends CoreException {
	private static final long serialVersionUID = 1L;

	public UserNotMatchedException(Code code, String[] detail) {
		super(code, detail);
	}
}