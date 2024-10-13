package com.whooa.blog.admin.service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserSearchRequest;
import com.whooa.blog.util.PaginationParam;

public interface AdminUserService {
	public abstract void delete(Long id);
	public abstract UserResponse find(Long id);
	public abstract PageResponse<UserResponse> findAll(PaginationParam paginationUtil);
	public abstract PageResponse<UserResponse> search(UserSearchRequest userSearch, PaginationParam paginationUtil);
	public abstract UserResponse update(Long id, UserAdminUpdateRequest userAdminUpdate);
}