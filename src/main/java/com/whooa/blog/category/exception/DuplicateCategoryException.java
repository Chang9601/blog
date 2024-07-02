package com.whooa.blog.category.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class DuplicateCategoryException extends CoreException {
	public DuplicateCategoryException(Code code, String[] details) {
		super(code, details);
	}
}