package com.whooa.blog.admin.controller;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.admin.service.AdminUserService;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.dto.UserDto.UserAdminUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(
	name = "관리자(사용자) API"
)
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {
	private AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}
	
	@Operation(
		summary = "사용자 삭제(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping("/{id}")
	public ApiResponse<UserResponse> deleteUser(@PathVariable Long id) {
		adminUserService.delete(id);
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"사용자를 삭제했습니다."});
	}

	@Operation(
		summary = "사용자 조회(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.find(id), new String[] {"사용자를 조회했습니다."});
	}
	
	@Operation(
		summary = "사용자 목록 조회(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<PageResponse<UserResponse>> getUsers(PaginationUtil pagination) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.findAll(pagination), new String[] {"사용자 목록을 조회했습니다."});
	}
	
	@Operation(
		summary = "사용자 수정(관리자)"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)	
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/{id}")
	public ApiResponse<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody(required = true) UserAdminUpdateRequest userAdminUpdate) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), adminUserService.update(id, userAdminUpdate), new String[] {"사용자를 수정했습니다."});
	}
}