package com.whooa.blog.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.CurrentUser;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
	name = "댓글 API"
)
@RestController
@RequestMapping("/api/v1/posts")
public class CommentController {
	private CommentService commentService;
	
	public CommentController( CommentService commentService) {
		this.commentService = commentService;
	}

	@Operation(
		summary = "댓글 생성"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/{post-id}/comments")
	public ApiResponse<CommentResponse> createComment(@PathVariable("post-id") Long postId, @Valid @RequestBody CommentCreateRequest commentCreate, @CurrentUser UserDetailsImpl userDetailsImpl) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.create(postId, commentCreate, userDetailsImpl), new String[] {"댓글을 생성했습니다."});
	}
	
	@Operation(
		summary = "댓글 삭제"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{post-id}/comments/{id}")
	public ApiResponse<CommentResponse> deleteComment(@PathVariable("id") Long id, @PathVariable("post-id") Long postId, @CurrentUser UserDetailsImpl userDetailsImpl) {
		commentService.delete(id, postId, userDetailsImpl);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"댓글을 삭제했습니다."});
	}

	@Operation(
		summary = "댓글 목록 조회"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{post-id}/comments")
	public ApiResponse<PageResponse<CommentResponse>> getCommentsByPostId(@PathVariable("post-id") Long postId, PaginationUtil paginationUtil) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), commentService.findAllByPostId(postId, paginationUtil), new String[] {"포스트의 댓글 목록을 조회했습니다."});
	}
	
	@Operation(
		summary = "대댓글 생성"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)		
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/{post-id}/comments/{id}")
	public ApiResponse<CommentResponse> replyComment(@PathVariable("id") Long id, @PathVariable("post-id") Long postId, @Valid @RequestBody CommentCreateRequest commentCreate, @CurrentUser UserDetailsImpl userDetailsImpl) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.reply(id, postId, commentCreate, userDetailsImpl), new String[] {"댓글에 답했습니다."});
	}

	@Operation(
		summary = "댓글 수정"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)	
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{post-id}/comments/{id}")
	public ApiResponse<CommentResponse> updateComment(@PathVariable("id") Long id, @PathVariable("post-id") Long postId, @Valid @RequestBody CommentUpdateRequest commentUpdate, @CurrentUser UserDetailsImpl userDetailsImpl) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), commentService.update(id, postId, commentUpdate, userDetailsImpl), new String[] {"댓글을 수정했습니다."});
	}
}