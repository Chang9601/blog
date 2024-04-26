package com.whooa.blog.user.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.security.jwt.JwtUtil;
import com.whooa.blog.common.type.JwtToken;
import com.whooa.blog.user.dto.UserDto.UserSignInRequest;
import com.whooa.blog.user.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
	private AuthenticationManager authenticationManager;
	private JwtUtil jwtUtil;
	
	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public JwtToken signIn(UserSignInRequest userSignIn) {
		String email = userSignIn.getEmail();
		String password = userSignIn.getPassword();
		
		System.out.println("이메일: " + email);
		System.out.println("비밀번호:" + password);
		
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		JwtToken token = jwtUtil.issue(email);
		
		return token;
	}
}