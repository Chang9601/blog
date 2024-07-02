package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class DirectoryNotCreatedException extends CoreException {
	public DirectoryNotCreatedException(Code code,  String[] details) {
		super(code, details);
	}
}