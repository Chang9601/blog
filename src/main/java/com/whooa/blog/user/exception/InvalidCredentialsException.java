package com.whooa.blog.user.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class InvalidCredentialsException extends AbstractException {
	public InvalidCredentialsException(Code exception, String[] details) {
		super(exception, details);
	}
}