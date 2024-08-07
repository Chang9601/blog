package com.whooa.blog.user.service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDTO.UserCreateRequest;
import com.whooa.blog.user.dto.UserDTO.UserResponse;
import com.whooa.blog.util.PaginationUtil;

public interface UserService {
	public abstract UserResponse create(UserCreateRequest userCreate);
	public abstract void delete(UserDetailsImpl userDetailsImpl);
	public abstract UserResponse find(UserDetailsImpl userDetailsImpl);
	public abstract PageResponse<UserResponse> findAll(PaginationUtil paginationUtil);
	public abstract UserResponse findByEmail(String email);
	public abstract UserResponse findById(Long id);
	public abstract UserResponse update(UserCreateRequest userCreate);
	//public abstract UserResponse resetPassword();
}