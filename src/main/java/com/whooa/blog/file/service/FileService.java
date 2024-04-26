package com.whooa.blog.file.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.file.dto.FileDto;
import com.whooa.blog.post.entity.PostEntity;

public interface FileService {
	public abstract FileDto upload(MultipartFile file, PostEntity postEntity);
	public abstract Resource downalod(String fileName);
}