package com.whooa.blog.user.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.whooa.blog.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	public abstract boolean existsByEmail(String email);
	public abstract Page<UserEntity> findByActiveTrue(Pageable pageable);
	public abstract Optional<UserEntity> findByEmail(String email);
	public abstract Optional<UserEntity> findByIdAndActiveTrue(Long id);
}