package com.whooa.blog.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.type.JwtToken;
import com.whooa.blog.user.dto.UserDto.UserSignInRequest;
import com.whooa.blog.user.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	
	private AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PostMapping("/sign-in")
	public ApiResponse<JwtToken> signIn(@RequestBody UserSignInRequest userSignIn) {
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), authService.signIn(userSignIn), new String[] {"로그인에 성공했습니다."});
	}
}