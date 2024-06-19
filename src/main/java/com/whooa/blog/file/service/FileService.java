package com.whooa.blog.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.file.value.File;
import com.whooa.blog.post.entity.PostEntity;

public interface FileService {
	public abstract File upload(PostEntity postEntity, MultipartFile uploadFile);
	public abstract Resource downalod(String fileName);
}