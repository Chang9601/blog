package com.whooa.blog.post.service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;

public interface PostElasticsearchService {
	public abstract PostResponse create(PostCreateRequest postCreate, UserDetailsImpl userDetailsImpl);
	public abstract PostResponse find(Long id);
	public abstract PageResponse<PostResponse> findByContent(String content);
	public abstract PageResponse<PostResponse> findByTitle(String title);
	public abstract PageResponse<PostResponse> findByCategoryName(String categoryName);
}
