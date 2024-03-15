package com.whooa.blog.post.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.service.PostService;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

	private final PostService postService;
	
	public PostController(final PostService postService) {
		this.postService = postService;
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<PostDto.Response> createPost(@RequestBody final PostDto.Request postDto) {
		return postService.createOne(postDto);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<PostDto.Response>> getPosts(final PageDto pageDto) {
		return postService.findAll(pageDto);
	}
	

	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<PostDto.Response> getPost(@PathVariable final Long id) {
		return postService.findOne(id);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/{id}")
	public ApiResponse<PostDto.Response> updatePost(@RequestBody final PostDto.Request postDto,  @PathVariable final Long id) {
		return postService.updateOne(postDto, id);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostDto.Response> deletePost(@PathVariable final Long id) {
		return postService.deleteOne(id);
	}	
}
