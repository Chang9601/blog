package com.whooa.blog.common.security.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class InvalidJwtRefreshTokenException extends CoreException {
	private static final long serialVersionUID = 1L;

	public InvalidJwtRefreshTokenException(Code code, String[] detail) {
		super(code, detail);
	}
}