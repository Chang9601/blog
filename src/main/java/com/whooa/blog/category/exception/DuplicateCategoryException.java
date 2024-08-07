package com.whooa.blog.category.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class DuplicateCategoryException extends CoreException {
	private static final long serialVersionUID = 1L;

	public DuplicateCategoryException(Code code, String[] details) {
		super(code, details);
	}
}