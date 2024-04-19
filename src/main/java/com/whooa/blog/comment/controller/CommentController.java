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

import com.whooa.blog.comment.dto.CommentDto;
import com.whooa.blog.comment.service.CommentService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CommentController {
	private final CommentService commentService;
	
	public CommentController(final CommentService commentService) {
		this.commentService = commentService;
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/posts/{post-id}/comments")
	public ApiResponse<CommentDto.Response> createComment(@PathVariable("post-id") final Long postId, @Valid @RequestBody final CommentDto.CreateRequest commentDto) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.create(postId, commentDto), null);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/posts/{post-id}/comments")
	public ApiResponse<List<CommentDto.Response>> getCommentsByPostId(@PathVariable("post-id") final Long postId) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), commentService.findAllByPostId(postId), null);
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PutMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentDto.Response> updateComment(@PathVariable("post-id") final Long postId, @PathVariable("comment-id") final Long commentId, @Valid @RequestBody final CommentDto.UpdateRequest commentDto) {		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(),  commentService.update(postId, commentId, commentDto), null);
	}	
	
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentDto.Response> deleteComment(@PathVariable("post-id") final Long postId, @PathVariable("comment-id") final Long commentId) {
		commentService.delete(postId, commentId);
				
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"댓글이 삭제되었습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping("/posts/{post-id}/comments/{comment-id}")
	public ApiResponse<CommentDto.Response> replyComment(@PathVariable("post-id") final Long postId, @PathVariable("comment-id") final Long commentId, @Valid @RequestBody final CommentDto.CreateRequest commentDto) {		
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), commentService.reply(postId, commentId, commentDto), null);
	}	
}