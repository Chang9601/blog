package com.whooa.blog.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.CurrentUser;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserPasswordUpdateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserUpdateRequest;
import com.whooa.blog.user.service.UserService;

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
		summary = "회원가입"
	)
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<UserResponse> createMe(@Valid @RequestBody(required = true) UserCreateRequest userCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), userService.create(userCreate), new String[] {"회원가입 했습니다."});
	}

	@Operation(
		summary = "회원탈퇴"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping
	public ApiResponse<UserResponse> deleteMe(@CurrentUser UserDetailsImpl userDetailsImpl) {
		userService.delete(userDetailsImpl);
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"회원탈퇴 했습니다."});
	}

	@Operation(
		summary = "회원정보"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<UserResponse> getMe(@CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.find(userDetailsImpl), new String[] {"회원정보를 조회했습니다."});
	}

	@Operation(
		summary = "회원수정"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping
	public ApiResponse<UserResponse> updateMe(@Valid @RequestBody(required = true) UserUpdateRequest userUpdate, @CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.update(userUpdate, userDetailsImpl), new String[] {"회원수정을 했습니다."});
	}
	
	@Operation(
		summary = "비밀번호 수정"
	)
	@SecurityRequirement(
		name = "JWT Cookie Authentication"
	)
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/update-my-password")
	public ApiResponse<UserResponse> updateMyPassword(@Valid @RequestBody(required = true) UserPasswordUpdateRequest userPasswordUpdate, @CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.updatePassowrd(userPasswordUpdate, userDetailsImpl), new String[] {"비밀번호를 수정했습니다."});
	}
}