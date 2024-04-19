package com.whooa.blog.utils;

public class NotNullNotEmptyChecker {
	public static boolean check(final String str) {
		return str != null && !str.isEmpty();
	}
}