package com.whooa.blog.user.service;

import com.whooa.blog.common.type.JwtToken;
import com.whooa.blog.user.dto.UserDto.UserSignInRequest;

public interface AuthService {
	public abstract JwtToken signIn(UserSignInRequest userSignIn);
}