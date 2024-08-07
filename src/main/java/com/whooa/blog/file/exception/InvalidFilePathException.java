package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class InvalidFilePathException extends CoreException {
	private static final long serialVersionUID = 1L;

	public InvalidFilePathException(Code code,  String[] details) {
		super(code, details);
	}
}