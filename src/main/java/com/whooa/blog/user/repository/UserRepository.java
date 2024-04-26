package com.whooa.blog.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	public abstract Optional<UserEntity> findByEmail(String email);
	public abstract Boolean existsByEmail(String email);
}