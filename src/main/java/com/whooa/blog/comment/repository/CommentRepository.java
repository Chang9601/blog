package com.whooa.blog.comment.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.comment.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
	public abstract Page<CommentEntity> findByPostId(Long postId, Pageable pageable);
}