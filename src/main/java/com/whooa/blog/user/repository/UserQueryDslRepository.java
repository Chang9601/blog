package com.whooa.blog.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.param.UserSearchParam;

public interface UserQueryDslRepository {
	public abstract Page<UserEntity> searchAll(UserSearchParam userSearchParam, Pageable pageable);
}