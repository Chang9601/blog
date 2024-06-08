package com.whooa.blog.user.service;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
	public abstract void signOut(HttpServletRequest request);
}