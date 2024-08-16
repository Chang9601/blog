package com.whooa.blog.user.service;

import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserPasswordUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserUpdateRequest;

public interface UserService {
	public abstract UserResponse create(UserCreateRequest userCreate);
	public abstract void delete(UserDetailsImpl userDetailsImpl);
	public abstract UserResponse find(UserDetailsImpl userDetailsImpl);
	public abstract UserResponse update(UserUpdateRequest userUpdate, UserDetailsImpl userDetailsImpl);
	public abstract UserResponse updatePassowrd(UserPasswordUpdateRequest userPasswordUpdate, UserDetailsImpl userDetailsImpl);
}