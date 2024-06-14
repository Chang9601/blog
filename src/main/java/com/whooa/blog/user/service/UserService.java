package com.whooa.blog.user.service;

import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;

public interface UserService {
	public abstract UserResponse create(UserCreateRequest userCreate);
	//public abstract UserResponse update(UserCreateRequest userCreate);
	//public abstract UserResponse delete(UserCreateRequest userCreate);
	//public abstract UserResponse resetPassword();
	public abstract UserResponse find(UserDetailsImpl userDetailsImpl);
	public abstract UserResponse findByEmail(String email);
}