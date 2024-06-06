package com.whooa.blog.file.exception;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.exception.CoreException;

public class FileNotDownloadedException extends CoreException {
	public FileNotDownloadedException(Code code,  String[] details) {
		super(code, details);
	}
}