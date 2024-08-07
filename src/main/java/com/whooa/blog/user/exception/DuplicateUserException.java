package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class DuplicateUserException extends CoreException {
	private static final long serialVersionUID = 1L;

	public DuplicateUserException(Code code, String[] details) {
		super(code, details);
	}
}