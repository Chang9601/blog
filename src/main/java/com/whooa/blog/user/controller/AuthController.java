package com.whooa.blog.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.type.JwtToken;
import com.whooa.blog.common.type.JwtType;
import com.whooa.blog.user.dto.UserDto.UserCreateRequest;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.dto.UserDto.UserSignInRequest;
import com.whooa.blog.user.service.AuthService;
import com.whooa.blog.utils.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
	private AuthService authService;
	
	public AuthController(AuthService authService) {
		this.authService = authService;
	}
	
	@ResponseStatus(value = HttpStatus.OK)
	@PostMapping("/sign-out")
	public ApiResponse<UserResponse> signOut(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		authService.signOut(request);
		
		System.out.println("hello?");
		
		return ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), null, new String[] {"로그아웃 했습니다."});
	}
}
