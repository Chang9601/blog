package com.whooa.blog.post.service;

import java.util.List;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.entity.Post;

public interface PostService {
	// 인터페이스의 메서드는 기본적으로 public 접근 제한자를 사용하고 정적이다.
	ApiResponse<PostDto.Response> create(Post post);
	ApiResponse<List<PostDto.Response>> findAll();
	ApiResponse<PostDto.Response> findOne(Long id);
	ApiResponse<PostDto.Response> updateOne(Post post, Long id);
	ApiResponse<PostDto.Response> deleteOne(Long id);
}
