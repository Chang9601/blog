package com.whooa.blog.admin.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.admin.service.AdminCommentService;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(description = "댓글 수정/삭제를 수행하는 관리자 댓글 컨트롤러", name = "관리자(댓글) API")
@RestController
@RequestMapping("/api/v1/admin/posts")
public class AdminCommentController {
	private AdminCommentService adminCommentService;

	public AdminCommentController(AdminCommentService adminCommentService) {
		this.adminCommentService = adminCommentService;
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 댓글 삭제", method = "DELETE", summary = "댓글 삭제")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "댓글 삭제 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{post-id}/comments/{id}")
	public ApiResponse<CommentResponse> deleteComment(@PathVariable("id") Long id, @PathVariable("post-id") Long postId) {
		adminCommentService.delete(id, postId);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"댓글을 삭제했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "아이디에 해당하는 댓글 수정", method = "PATCH", summary = "댓글 수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "댓글 수정 성공", responseCode = "200")
	@Parameter(description = "댓글", example = "하이!", name = "content")
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{post-id}/comments/{id}")
	public ApiResponse<CommentResponse> updateComment(@PathVariable("id") Long id, @PathVariable("post-id") Long postId, @Valid @RequestBody CommentUpdateRequest commentUpdate) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminCommentService.update(id, postId, commentUpdate), new String[] {"댓글을 수정했습니다."});
	}
}