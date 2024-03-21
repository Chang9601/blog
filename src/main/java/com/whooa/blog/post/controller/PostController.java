package com.whooa.blog.post.controller;

import java.util.List;

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
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.mapper.PostMapper;
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
		return postService.create(PostMapper.INSTANCE.toEntity(postDto));
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<List<PostDto.Response>> getAllPosts() {
		return postService.findAll();
	}
	

	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<PostDto.Response> getPost(@PathVariable Long id) {
		return postService.findOne(id);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/{id}")
	public ApiResponse<PostDto.Response> updatePost(@RequestBody PostDto.Request postDto,  @PathVariable Long id) {
		return postService.updateOne(PostMapper.INSTANCE.toEntity(postDto), id);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostDto.Response> deletePost(@PathVariable Long id) {
		return postService.deleteOne(id);
	}	
}
