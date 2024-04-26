package com.whooa.blog.common.type;

public enum JwtType {
	ACCESS_TOKEN("AccessToken"), REFRESH_TOKEN("RefresToken");
	
	private String type;
	
	private JwtType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
}