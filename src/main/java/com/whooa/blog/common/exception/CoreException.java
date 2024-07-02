package com.whooa.blog.common.exception;

import com.whooa.blog.common.code.Code;

public abstract class CoreException extends RuntimeException {
	private Code code;
	private String[] detail;
	
	public CoreException(Code code, String[] detail) {
		this.code = code;
		this.detail = detail;
	}
	
	public Code getCode() {
		return code;
	}

	public String[] getDetail() {
		return detail;
	}
}