package com.whooa.blog.post.service;


import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;

public interface PostService {
	// 인터페이스의 메서드는 기본적으로 public 접근 제한자를 사용하고 정적이다.
	public abstract PostDto.Response create(final PostDto.CreateRequest postDto);
	public abstract PageResponse<PostDto.Response>  findAll(final PageDto pageDto);
	public abstract PostDto.Response find(final Long id);
	public abstract PostDto.Response update(final PostDto.UpdateRequest postDto, final Long id);
	public abstract void delete(final Long id);
}
