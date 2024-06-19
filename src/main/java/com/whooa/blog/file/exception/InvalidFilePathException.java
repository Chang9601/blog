package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class InvalidFilePathException extends AbstractException {
	public InvalidFilePathException(Code code,  String[] details) {
		super(code, details);
	}
}