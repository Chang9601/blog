package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class FileNotStoredException extends AbstractException {

	public FileNotStoredException(Code exception,  String[] details) {
		super(exception, details);
	}
}