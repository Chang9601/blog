package com.whooa.blog.category.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class DuplicateCategoryException extends AbstractException {
	public DuplicateCategoryException(Code exception, String[] details) {
		super(exception, details);
	}
}