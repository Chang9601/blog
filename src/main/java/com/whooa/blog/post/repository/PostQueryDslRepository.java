package com.whooa.blog.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.post.entity.PostEntity;

public interface PostQueryDslRepository {
	public abstract Page<PostEntity> findAll(Pageable pageable);
}