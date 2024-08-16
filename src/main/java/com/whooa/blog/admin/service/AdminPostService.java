package com.whooa.blog.admin.service;

import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;

public interface AdminPostService {
	public abstract void delete(Long id);
	public abstract PostResponse update(Long id, PostUpdateRequest postUpdate, MultipartFile[] uploadFiles);
}