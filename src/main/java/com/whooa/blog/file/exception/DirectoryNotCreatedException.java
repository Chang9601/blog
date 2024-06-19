package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class DirectoryNotCreatedException extends AbstractException {
	public DirectoryNotCreatedException(Code code,  String[] details) {
		super(code, details);
	}
}