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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

// TODO: 비밀번호 재설정 API, 회원정보에서 포스트 댓글까지 가져오는 API
@Tag(description = "회원가입/정보/수정/탈퇴 및 비밀번호 수정을 수행하는 사용자 컨트롤러", name = "관리자(사용자) API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "회원가입", method = "POST", summary = "회원가입")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "회원가입 성공", responseCode = "201")
	@Parameters({
		@Parameter(description = "이메일", example = "user1@naver.com", name = "email"),
		@Parameter(description = "이름", example = "사용자1", name = "name"),
		@Parameter(description = "비밀번호", example = "12341234Aa!@", name = "password"),
		@Parameter(description = "역할", example = "USER", name = "userRole"),
	})
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping
	public ApiResponse<UserResponse> createMe(@Valid @RequestBody(required = true) UserCreateRequest userCreate) {
		return ApiResponse.handleSuccess(Code.CREATED.getCode(), Code.CREATED.getMessage(), userService.create(userCreate), new String[] {"회원가입 했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "회원탈퇴", method = "DELETE", summary = "회원탈퇴")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "회원탈퇴 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@DeleteMapping
	public ApiResponse<UserResponse> deleteMe(@CurrentUser UserDetailsImpl userDetailsImpl) {
		userService.delete(userDetailsImpl);
		
		return ApiResponse.handleSuccess(Code.NO_CONTENT.getCode(), Code.NO_CONTENT.getMessage(), null, new String[] {"회원탈퇴 했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "회원정보", method = "GET", summary = "회원정보")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "회원정보 성공", responseCode = "200")
	@ResponseStatus(value = HttpStatus.OK)
	@GetMapping
	public ApiResponse<UserResponse> getMe(@CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.find(userDetailsImpl), new String[] {"회원정보를 조회했습니다."});
	}

	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "회원수정", method = "PATCH", summary = "회원수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "회원수정 성공", responseCode = "200")
	@Parameters({
		@Parameter(description = "이메일", example = "user1@naver.com", name = "email"),
		@Parameter(description = "이름", example = "사용자1", name = "name"),
		@Parameter(description = "비밀번호", example = "12341234Aa!@", name = "password"),
		@Parameter(description = "역할", example = "USER", name = "userRole"),
	})
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping
	public ApiResponse<UserResponse> updateMe(@Valid @RequestBody(required = true) UserUpdateRequest userUpdate, @CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.update(userUpdate, userDetailsImpl), new String[] {"회원수정을 했습니다."});
	}
	
	@SecurityRequirement(name = "JWT Cookie Authentication")
	@Operation(description = "비밀번호 수정", method = "PATCH", summary = "비밀번호 수정")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(content = @Content(mediaType = "application/json"), description = "비밀번호 수정 성공", responseCode = "200")
	@Parameters({
		@Parameter(description = "구 비밀번호", example = "12341234Aa!@", name = "oldPassword"),
		@Parameter(description = "신 비밀번호", example = "43218765Aa!@", name = "newPassword"),
	})
	@ResponseStatus(value = HttpStatus.OK)
	@PatchMapping("/update-my-password")
	public ApiResponse<UserResponse> updateMyPassword(@Valid @RequestBody(required = true) UserPasswordUpdateRequest userPasswordUpdate, @CurrentUser UserDetailsImpl userDetailsImpl) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userService.updatePassowrd(userPasswordUpdate, userDetailsImpl), new String[] {"비밀번호를 수정했습니다."});
	}
}