package com.whooa.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// OK
public class PasswordUtil {
	private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	
	public static BCryptPasswordEncoder bCryptPasswordEncoder() {
		return bCryptPasswordEncoder;
	}

	public static String hash(String password) {
		return bCryptPasswordEncoder().encode(password);
	}
	
	public static boolean match(String plainPassword, String hashedPassword) {
		return bCryptPasswordEncoder().matches(plainPassword, hashedPassword);
	}
}