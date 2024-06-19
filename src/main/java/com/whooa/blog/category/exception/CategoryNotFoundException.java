package com.whooa.blog.category.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class CategoryNotFoundException extends AbstractException {
	public CategoryNotFoundException(Code code, String[] details) {
		super(code, details);
	}
}