package com.whooa.blog.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.api.PageResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDTO.UserCreateRequest;
import com.whooa.blog.user.dto.UserDTO.UserResponse;
import com.whooa.blog.user.service.UserService;
import com.whooa.blog.util.PaginationUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(
	name = "사용자 API"
)
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(
		summary = "사용자 생성"
	)
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<UserResponse> createUser(@Valid @RequestBody(required = true) UserCreateRequest userCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), userService.create(userCreate), new String[] {"사용자를 생성했습니다."});
	}

	@Operation(
		summary = "사용자 삭제"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping
	public ApiResponse<UserResponse> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		userService.delete(userDetailsImpl);
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"사용자를 삭제했습니다."});
	}

	@Operation(
		summary = "본인 조회"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/me")
	public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.find(userDetailsImpl), new String[] {"본인을 조회했습니다."});
	}

	@Operation(
		summary = "사용자 조회"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/{id}")
	public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.findById(id), new String[] {"사용자를 조회했습니다."});
	}
	
	@Operation(
		summary = "사용자 목록 조회"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)	
	@ResponseStatus(value = HttpStatus.OK)
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public ApiResponse<PageResponse<UserResponse>> getUsers(PaginationUtil pagination) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.findAll(pagination), new String[] {"사용자 목록을 조회했습니다."});
	}
}