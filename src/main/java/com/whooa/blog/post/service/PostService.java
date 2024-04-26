package com.whooa.blog.post.service;


import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;

public interface PostService {
	/* 인터페이스의 메서드는 기본적으로 public 접근 제한자를 사용하고 정적이다. */
	public abstract PostResponse create(PostCreateRequest postCreate, MultipartFile[] uploadFiles);
	public abstract PageResponse<PostResponse> findAll(PageDto pageDto);
	public abstract PostResponse find(Long id);
	public abstract PostResponse update(PostUpdateRequest postUpdate, Long id);
	public abstract void delete(Long id);
}
