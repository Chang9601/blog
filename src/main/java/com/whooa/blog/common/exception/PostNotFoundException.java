package com.whooa.blog.common.exception;

public class PostNotFoundException extends BaseException {
    private static final long serialVersionUID = 1L;

    private String fieldName;
    private String fieldValue;

	public PostNotFoundException(final int code, final String message) {
		super(code, message);
	}
	
	public PostNotFoundException(final int code, final String message, final String fieldName, final String fieldValue) {
		super(code, message);
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldValue() {
		return fieldValue;
	}
}