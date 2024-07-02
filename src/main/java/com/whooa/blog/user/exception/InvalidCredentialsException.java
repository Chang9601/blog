package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class InvalidCredentialsException extends CoreException {
	public InvalidCredentialsException(Code code, String[] details) {
		super(code, details);
	}
}