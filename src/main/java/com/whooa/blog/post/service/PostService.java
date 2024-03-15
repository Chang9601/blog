package com.whooa.blog.post.service;


import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;

public interface PostService {
	// 인터페이스의 메서드는 기본적으로 public 접근 제한자를 사용하고 정적이다.
	public abstract ApiResponse<PostDto.Response> createOne(final PostDto.Request postDto);
	public abstract ApiResponse<PageResponse<PostDto.Response>> findAll(final PageDto pageDto);
	public abstract ApiResponse<PostDto.Response> findOne(final Long id);
	public abstract ApiResponse<PostDto.Response> updateOne(final PostDto.Request postDto, final Long id);
	public abstract ApiResponse<PostDto.Response> deleteOne(final Long id);
}
