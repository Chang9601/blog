package com.whooa.blog.post.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.CurrentUser;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.post.dto.PostDto.PostCreateRequest;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.service.PostElasticsearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(
	name = "포스트 API"
)
@RestController
@RequestMapping("/api/v1/es/posts")
public class PostElasticsearchController {
	private PostElasticsearchService postElasticsearchService;

	public PostElasticsearchController(PostElasticsearchService postElasticsearchService) {
		this.postElasticsearchService = postElasticsearchService;
	}
	
	@Operation(
			summary = "포스트 생성"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<PostResponse> createPost(@Valid @RequestBody PostCreateRequest postCreate, @CurrentUser UserDetailsImpl userDetailsImpl) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), postElasticsearchService.create(postCreate, userDetailsImpl), new String[] {"포스트를 생성했습니다."});
	}

	@Operation(
		summary = "포스트 조회"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<PostResponse> getPost(@PathVariable Long id) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), postElasticsearchService.find(id), new String[] {"포스트를 조회했습니다."});
	}	
}