package com.whooa.blog.common.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.type.JwtType;
import com.whooa.blog.utils.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutHandler implements LogoutHandler {
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
			// TODO: 블랙 리스트.
			Cookie[] cookies = request.getCookies();
			
			if (cookies != null) {
				for (Cookie cookie: request.getCookies()) {
					String name = cookie.getName();
					
					if (name.equals(JwtType.ACCESS_TOKEN.getType())) {
						CookieUtil.clear(cookie, "/");
						response.addCookie(cookie);
					}
					
					if (name.equals(JwtType.REFRESH_TOKEN.getType())) {
						CookieUtil.clear(cookie, "/");
						response.addCookie(cookie);
					}
				}
			}
	}
}