package com.whooa.blog.util;

public class NotNullNotEmptyChecker {
	public static boolean check(String input) {
		return input != null && !input.isEmpty();
	}
}