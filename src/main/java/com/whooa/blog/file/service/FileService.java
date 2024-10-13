package com.whooa.blog.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.file.value.File;

public interface FileService<T> {
	public abstract File upload(T entity, MultipartFile uploadFile);
	public abstract Resource downalod(String fileName);
}