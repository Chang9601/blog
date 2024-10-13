package com.whooa.blog.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.comment.dto.CommentDto.CommentSearchRequest;
import com.whooa.blog.comment.entity.CommentEntity;

public interface CommentQueryDslRepository {
	public abstract Page<CommentEntity> search(CommentSearchRequest commentSearch, Pageable pageable);
}