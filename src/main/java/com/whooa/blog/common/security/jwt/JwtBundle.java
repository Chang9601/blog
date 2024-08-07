package com.whooa.blog.common.security.jwt;

public class JwtBundle {
	private String accessToken;
	private String refreshToken;
	
	public JwtBundle(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	@Override
	public String toString() {
		return "JwtBundle [accessToken=" + accessToken + ", refreshToken=" + refreshToken + "]";
	}
}