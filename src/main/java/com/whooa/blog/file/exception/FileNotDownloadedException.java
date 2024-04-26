package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.AbstractException;

public class FileNotDownloadedException extends AbstractException {

	public FileNotDownloadedException(Code exception,  String[] details) {
		super(exception, details);
	}
}