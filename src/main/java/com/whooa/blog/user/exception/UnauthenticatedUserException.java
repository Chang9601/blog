package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class UnauthenticatedUserException extends CoreException {
	public UnauthenticatedUserException(Code code, String[] details) {
		super(code, details);
	}
}