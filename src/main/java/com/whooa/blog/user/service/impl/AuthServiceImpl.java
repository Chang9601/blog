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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {
	private AuthenticationManager authenticationManager;
	private JwtUtil jwtUtil;
	
	public AuthServiceImpl(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void signOut(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie: request.getCookies()) {
				String name = cookie.getName();
				
				System.out.println(name);
				
				if (name.equals("AccessToken")) {
					cookie.setMaxAge(0);
				}
				
				if (name.equals("RefreshToken")) {
					cookie.setMaxAge(0);
				}
			}
		}		
	}
}