package com.whooa.blog.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public PasswordUtil(BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public String hash(String password) {
		return bCryptPasswordEncoder.encode(password);
	}
	
	public boolean match(String plainPassword, String hashedPassword) {
		return bCryptPasswordEncoder.matches(plainPassword, hashedPassword);
	}
}