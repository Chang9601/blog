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
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.dto.PageDto;
import com.whooa.blog.post.dto.PostDto;
import com.whooa.blog.post.service.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
	private final PostService postService;
	
	public PostController(final PostService postService) {
		this.postService = postService;
	}
	
	// @Valid 어노테이션은 Hibernate 검증자(validator)를 활성화한다.
	@ResponseStatus(value = HttpStatus.CREATED) 
	@PostMapping
	public ApiResponse<PostDto.Response> createPost(@Valid @RequestBody final PostDto.CreateRequest postDto) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), postService.create(postDto), null);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<PostDto.Response>> getPosts(final PageDto pageDto) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postService.findAll(pageDto), null);
	}
	

	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<PostDto.Response> getPost(@PathVariable final Long id) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(),  postService.find(id), null);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/{id}")
	public ApiResponse<PostDto.Response> updatePost(@Valid @RequestBody final PostDto.UpdateRequest postDto, @PathVariable final Long id) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postService.update(postDto, id), null);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostDto.Response> deletePost(@PathVariable final Long id) {
		postService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"포스트가 삭제되었습니다."});
	}	
}