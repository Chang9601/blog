package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class FileNotFoundException extends AbstractException {
	public FileNotFoundException(Code code,  String[] details) {
		super(code, details);
	}
}