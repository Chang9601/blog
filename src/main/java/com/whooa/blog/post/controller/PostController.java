package com.whooa.blog.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

	private final PostService postService;
	
	public PostController(PostService postService) {
		this.postService = postService;
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<PostDto.Response> createPost(@RequestBody final PostDto.Request postDto) {
		return postService.create(postDto);
	}
}
