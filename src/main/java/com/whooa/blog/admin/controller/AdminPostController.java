package com.whooa.blog.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(description = "포스트 수정/삭제를 수행하는 관리자 포스트 컨트롤러", name = "관리자(포스트) API")
@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminPostController {
	private AdminPostService adminPostService;

	public AdminPostController(AdminPostService adminPostService) {
		this.adminPostService = adminPostService;
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 포스트 삭제", method = "DELETE", summary = "포스트 삭제")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "댓글 삭제 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<PostResponse> deletePost(@PathVariable Long id) {
		adminPostService.delete(id);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"포스트를 삭제했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 포스트 수정", method = "PATCH", summary = "포스트 수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "포스트 수정 성공", responseCode = "200")
	@Parameters({
		@Parameter(description = "카테고리 이름", example = "운영체제", name = "categoryName"),
		@Parameter(description = "포스트 내용", example = "100자 이상의 포스트", name = "content"),
		@Parameter(description = "포스트 제목", example = "프로세스와 스레드", name = "title"),
		@Parameter(description = "파일 목록", example = "Whistle.jpg", name = "files"),
	})
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
	public ApiResponse<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestPart(name = "post") PostUpdateRequest postUpdate, @RequestPart(name = "files", required = false) MultipartFile[] uploadFiles) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminPostService.update(id, postUpdate, uploadFiles), new String[] {"포스트를 수정했습니다."});
	}	
}