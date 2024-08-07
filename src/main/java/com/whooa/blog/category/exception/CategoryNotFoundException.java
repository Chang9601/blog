package com.whooa.blog.category.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class CategoryNotFoundException extends CoreException {
	private static final long serialVersionUID = 1L;

	public CategoryNotFoundException(Code code, String[] details) {
		super(code, details);
	}
}