package com.whooa.blog.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.comment.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
	public abstract List<CommentEntity> findByPostId(Long postId);
}