package com.whooa.blog.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.user.dto.UserDto.UserSearchRequest;
import com.whooa.blog.user.entity.UserEntity;

public interface UserQueryDslRepository {
	public abstract Page<UserEntity> search(UserSearchRequest userSearch, Pageable pageable);
}