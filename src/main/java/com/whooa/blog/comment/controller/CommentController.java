package com.whooa.blog.comment.controller;

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

import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
	private CommentService commentService;
	
	public CommentController( CommentService commentService) {
		this.commentService = commentService;
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/posts/{post-id}/comments")
	public ApiResponse<CommentResponse> createComment(@PathVariable("post-id") Long postId, @Valid @RequestBody CommentCreateRequest commentCreate) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.create(postId, commentCreate), new String[] {"댓글 생성했습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/posts/{post-id}/comments")
	public ApiResponse<List<CommentResponse>> getCommentsByPostId(@PathVariable("post-id") Long postId) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), commentService.findAllByPostId(postId), new String[] {"포스트의 댓글 목록을 조회했습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentResponse> updateComment(@PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId, @Valid @RequestBody CommentUpdateRequest commentUpdate) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), commentService.update(postId, commentId, commentUpdate), new String[] {"댓글을 수정했습니다."});
	}	
	
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentResponse> deleteComment(@PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId) {
		commentService.delete(postId, commentId);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"댓글이 삭제되었습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentResponse> replyComment(@PathVariable("post-id") Long postId, @PathVariable("comment-id") Long commentId, @Valid @RequestBody CommentCreateRequest commentCreate) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.reply(postId, commentId, commentCreate), new String[] {"댓글에 답했습니다."});
	}	
}