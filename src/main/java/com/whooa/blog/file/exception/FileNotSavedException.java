package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class FileNotSavedException extends AbstractException {
	public FileNotSavedException(Code code,  String[] details) {
		super(code, details);
	}
}