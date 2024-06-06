package com.whooa.blog.common.type;

public class JwtToken {
		private String accessToken;
		private String refreshToken;
		
		public JwtToken(String accessToken, String refreshToken) {
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
			return "JwtToken [accessToken=" + accessToken + ", refreshToken=" + refreshToken + "]";
		}
}