package com.whooa.blog.util;

import jakarta.servlet.http.Cookie;

public class CookieUtil {
	public static Cookie set(String name, String value, boolean httpOnly, int maxAge, String path, String sameSite, boolean secure) {
		Cookie cookie = new Cookie(name, value);
		
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(maxAge);
		cookie.setPath(path);
		cookie.setAttribute("SameSite", sameSite);
		cookie.setSecure(secure);
		
		return cookie;
	}
	
	/* 쿠키를 제거하려면 키와 값 옵션이 정확히 일치해야 한다. (단, expires 옵션과 maxAge 옵션은 제외.) */
	public static void clear(Cookie cookie, String path) {
		cookie.setMaxAge(0);
		cookie.setPath(path);
	}
}