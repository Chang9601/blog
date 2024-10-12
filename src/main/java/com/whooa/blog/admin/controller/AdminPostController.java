package com.whooa.blog.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.whooa.blog.admin.service.AdminPostService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.post.dto.PostDto.PostResponse;
import com.whooa.blog.post.dto.PostDto.PostUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(
	name = "관리자(포스트) API",
	description = "포스트 수정/삭제를 수행하는 관리자 포스트 컨트롤러"
)
@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminPostController {
	private AdminPostService adminPostService;

	public AdminPostController(AdminPostService adminPostService) {
		this.adminPostService = adminPostService;
	}
	
	@Operation(
		summary = "포스트 삭제(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostResponse> deletePost(@PathVariable Long id) {
		adminPostService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"포스트를 삭제했습니다."});
	}
	
	@Operation(
		summary = "포스트 수정(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{id}")
	public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestPart(name = "post") PostUpdateRequest postUpdate, @RequestPart(name = "files", required = false) MultipartFile[] uploadFiles) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminPostService.update(id, postUpdate, uploadFiles), new String[] {"포스트를 수정했습니다."});
	}	
}