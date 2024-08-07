package com.whooa.blog.common.security.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class JwtRefreshTokenNotMatched extends CoreException {
	private static final long serialVersionUID = 1L;

	public JwtRefreshTokenNotMatched(Code code, String[] detail) {
		super(code, detail);
	}
}