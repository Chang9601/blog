package com.whooa.blog.utils;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
	public static Cookie set(String key, String value, boolean httpOnly, int expiration, String sameSite, String path) {
		Cookie cookie = new Cookie(key, value);
		
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(expiration);
		cookie.setAttribute("SameSite", sameSite);
		cookie.setPath(path);
		
		return cookie;
	}
	
	public static void clear(Cookie cookie, String path) {
		cookie.setMaxAge(0);
		cookie.setPath(path);
	}
}