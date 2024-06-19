package com.whooa.blog.common.type;

public enum JwtType {
	ACCESS_TOKEN("ACCESS_TOKEN"), REFRESH_TOKEN("REFRESH_TOKEN");
	
	private String type;
	
	private JwtType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}