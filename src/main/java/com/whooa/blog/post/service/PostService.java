package com.whooa.blog.post.service;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.post.dto.PostDto;

public interface PostService {
	ApiResponse<PostDto.Response> createPost(PostDto.Request postDto);
}
