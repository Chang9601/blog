package com.whooa.blog.user.service;

import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;

public interface UserService {
	public abstract UserResponse create(UserCreateRequest userCreate);
	public abstract UserResponse findByEmail(String email);
}