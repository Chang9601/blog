package com.whooa.blog.admin.service;

import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.util.PaginationUtil;

public interface AdminUserService {
	public abstract void delete(Long id);
	public abstract UserResponse find(Long id);
	public abstract PageResponse<UserResponse> findAll(PaginationUtil paginationUtil);
	public abstract UserResponse update(Long id, UserAdminUpdateRequest userAdminUpdate);
}