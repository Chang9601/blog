package com.whooa.blog.utils;

import jakarta.servlet.http.Cookie;

public class CookieBuilder {

	public static Cookie build(String key, String value, boolean httpOnly, int expiration, String sameSite, String path) {
		Cookie cookie = new Cookie(key, value);
		
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(expiration);
		cookie.setAttribute("SameSite", sameSite);
		cookie.setPath(path);
		
		return cookie;
	}
}