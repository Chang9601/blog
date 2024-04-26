package com.whooa.blog.user.type;

public enum UserRole {
	
	USER(ROLE.USER, "사용자"),
	MANAGER(ROLE.MANAGER, "매니저"),
	ADMIN(ROLE.ADMIN, "관리자");
	
	public static class ROLE {
		public static String USER = "USER";
		public static String MANAGER = "MANAGER";
		public static String ADMIN = "ADMIN";
	}
	
	private String role;
	private String description;
	
	private UserRole(String role, String description) {
		this.role = role;
		this.description = description;
	}

	public String getRole() {
		return role;
	}

	public String getDescription() {
		return description;
	}
}