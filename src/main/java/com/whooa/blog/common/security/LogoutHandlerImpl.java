package com.whooa.blog.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.security.jwt.JwtType;
import com.whooa.blog.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogoutHandlerImpl implements LogoutHandler {
	@Override
	public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
			// TODO: 블랙 리스트.
			CookieUtil.clear(httpServletRequest, httpServletResponse, JwtType.ACCESS_TOKEN.getType());
			CookieUtil.clear(httpServletRequest, httpServletResponse, JwtType.REFRESH_TOKEN.getType());	
	}
}