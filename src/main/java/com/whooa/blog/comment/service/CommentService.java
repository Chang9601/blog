package com.whooa.blog.comment.service;


import com.whooa.blog.comment.dto.CommentDto.CommentCreateRequest;
import com.whooa.blog.comment.dto.CommentDto.CommentUpdateRequest;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.util.PaginationUtil;
import com.whooa.blog.comment.dto.CommentDto.CommentResponse;

public interface CommentService {
	public abstract CommentResponse create(Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl);
	public abstract void delete(Long id, Long postId, UserDetailsImpl userDetailsImpl);
	public abstract PageResponse<CommentResponse> findAllByPostId(Long postId, PaginationUtil paginationUtil);
	public abstract CommentResponse reply(Long id, Long postId, CommentCreateRequest commentCreate, UserDetailsImpl userDetailsImpl);
	public abstract CommentResponse update(Long id, Long postId, CommentUpdateRequest commentUpdate, UserDetailsImpl userDetailsImpl);
}