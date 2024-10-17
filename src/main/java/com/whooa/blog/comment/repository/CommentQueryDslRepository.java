package com.whooa.blog.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.comment.param.CommentSearchParam;

public interface CommentQueryDslRepository {
	public abstract Page<CommentEntity> searchAll(CommentSearchParam commentSearchParam, Pageable pageable);
}