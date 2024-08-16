package com.whooa.blog.common.security.jwt;

public enum JwtType {
	ACCESS_TOKEN("access_token"), REFRESH_TOKEN("refresh_token");
	
	private String type;
	
	private JwtType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}