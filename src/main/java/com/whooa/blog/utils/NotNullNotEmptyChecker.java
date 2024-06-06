package com.whooa.blog.utils;

public class NotNullNotEmptyChecker {
	public static boolean check(String input) {
		return input != null && !input.isEmpty();
	}
}