package com.whooa.blog.user.mapper;

import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.user.type.UserRole.ROLE;

public class UserRoleMapper {
	public static UserRole map(String input) {
		if (input == null) {
			return UserRole.USER;
		}
		
		String userRole = input.toUpperCase();
		
		switch (userRole) {
			case ROLE.USER:
				return UserRole.USER;
			case ROLE.MANAGER:
				return UserRole.MANAGER;
			case ROLE.ADMIN:
				return UserRole.ADMIN;
			default:
				return UserRole.USER;
		}
	}
}