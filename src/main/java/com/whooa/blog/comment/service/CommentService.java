package com.whooa.blog.comment.service;


import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentDeleteRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.dto.PageQueryString;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;

public interface CommentService {
	public abstract CommentResponse create(UserDetailsImpl userDetailsImpl, Long postId, CommentCreateRequest commentCreate);
	public abstract PageResponse<CommentResponse> findAllByPostId(Long postId, PageQueryString pageQueryString);
	public abstract CommentResponse update(Long postId, Long commentId, CommentUpdateRequest commentUpdate);
	public abstract void delete(Long postId, Long commentId, CommentDeleteRequest commentDelete);
	public abstract CommentResponse reply(Long postId, Long commentId, CommentCreateRequest commentCreate);
}