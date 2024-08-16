package com.whooa.blog.admin.service;

import com.whooa.blog.comment.dto.CommentDto.CommentResponse;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;

public interface AdminCommentService {
	public abstract void delete(Long id, Long postId);
	public abstract CommentResponse update(Long id, Long postId, CommentUpdateRequest commentUpdate);
}