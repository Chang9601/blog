package com.whooa.blog.util;

import java.util.Base64;
import java.util.Optional;

import org.springframework.util.SerializationUtils;

import com.whooa.blog.common.security.jwt.JwtType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
	public static void set(HttpServletResponse httpServletResponse, String name, String value, boolean httpOnly, int maxAge, String path, String sameSite, boolean secure) {
		Cookie cookie = new Cookie(name, value);
		
		cookie.setHttpOnly(httpOnly);
		cookie.setMaxAge(maxAge);
		cookie.setPath(path);
		cookie.setAttribute("SameSite", sameSite);
		cookie.setSecure(secure);
		
		httpServletResponse.addCookie(cookie);
		
		//return cookie;
	}
	
	/* 쿠키를 제거하려면 키와 값 옵션이 정확히 일치해야 한다. (단, expires 옵션과 maxAge 옵션은 제외.) */
	public static void clear(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String name, boolean httpOnly, String path, String sameSite, boolean secure) {
        Cookie[] cookies = httpServletRequest.getCookies();
        
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setHttpOnly(httpOnly);
                    cookie.setMaxAge(0);
                    cookie.setPath(path);
                    cookie.setAttribute("SameSite", sameSite);
                    cookie.setSecure(secure);
                    
                    httpServletResponse.addCookie(cookie);
                }
            }
        }
	}
	
	public static void setJwtCookies(HttpServletResponse httpServletResponse, String accessToken, String refreshToken) {
		set(httpServletResponse, JwtType.ACCESS_TOKEN.getType(), accessToken, true, 1 * 60 * 60, "/", "Strict", false);
		set(httpServletResponse, JwtType.REFRESH_TOKEN.getType(), refreshToken, true, 1 * 60 * 60 * 24 * 30, "/", "Strict", false);
	}
	
	public static void clearJwtCookies(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		clear(httpServletRequest, httpServletResponse, JwtType.ACCESS_TOKEN.getType(), true, "/", "Strict", false);
		clear(httpServletRequest, httpServletResponse, JwtType.REFRESH_TOKEN.getType(), true, "/", "Strict", false);
	}
	
	public static Optional<Cookie> get(HttpServletRequest httpServletRequest, String name) {
        Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie: cookies) {
				if (cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}
		
		return Optional.empty();
	}
	
	// TODO: SerializationUtil 클래스 대체.
	public static String serialize(Object object) {
		return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
		//return Base64.getUrlEncoder().encodeToString(SerializeDeserializeUtil.serializeToByte(object));
	}
	
	public static <T> T deserialize(Cookie cookie, Class<T> className) {
		return className.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
		//return SerializeDeserializeUtil.deserializeFromByte(Base64.getUrlDecoder().decode(cookie.getValue()), className);
	}
}