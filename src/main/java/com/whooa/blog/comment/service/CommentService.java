package com.whooa.blog.comment.service;

import java.util.List;

import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;

public interface CommentService {
	public abstract CommentResponse create(Long postId,  CommentCreateRequest commentCreate);
	public abstract List<CommentResponse> findAllByPostId(Long postId);
	public abstract CommentResponse update(Long postId,  Long commentId,  CommentUpdateRequest commentUpdate);
	public abstract void delete(Long postId,  Long commentId);
	public abstract CommentResponse reply(Long postId,  Long commentId,  CommentCreateRequest commentCreate);
}