package com.whooa.blog.comment.service;

import java.util.List;

import com.whooa.blog.comment.dto.CommentDto;

public interface CommentService {
	public abstract CommentDto.Response create(final Long postId, final CommentDto.CreateRequest commentDto);
	public abstract List<CommentDto.Response> findAllByPostId(final Long postId);
	public abstract CommentDto.Response update(final Long postId, final Long commentId, final CommentDto.UpdateRequest commentDto);
	public abstract void delete(final Long postId, final Long commentId);
	public abstract CommentDto.Response reply(final Long postId, final Long commentId, final CommentDto.CreateRequest commentDto);
}