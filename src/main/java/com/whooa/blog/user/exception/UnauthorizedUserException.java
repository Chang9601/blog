package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class UnauthorizedUserException extends CoreException {
	private static final long serialVersionUID = 1L;

	public UnauthorizedUserException(Code code, String[] details) {
		super(code, details);
	}
}