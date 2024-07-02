package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class InvalidFilePathException extends CoreException {
	public InvalidFilePathException(Code code,  String[] details) {
		super(code, details);
	}
}