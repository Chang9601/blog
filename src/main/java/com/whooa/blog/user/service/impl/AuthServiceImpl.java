package com.whooa.blog.user.service.impl;

import org.springframework.stereotype.Service;


import com.whooa.blog.user.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthServiceImpl implements AuthService {


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