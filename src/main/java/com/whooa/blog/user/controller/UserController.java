package com.whooa.blog.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<UserResponse> createUser(@RequestBody UserCreateRequest userCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), userService.create(userCreate), new String[] {"사용자가 생성되었습니다."});
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping("/me")
	public ApiResponse<UserResponse> getMe(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.find(userDetailsImpl), new String[] {"본인을 조회했습니다."});
	}
}