package com.whooa.blog.utils;

public class NotNullNotEmptyChecker {
	public static boolean check(String str) {
		return str != null && !str.isEmpty();
	}
}