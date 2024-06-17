package com.whooa.blog.user.dto;

import com.whooa.blog.user.type.UserRole;

import jakarta.validation.constraints.NotBlank;

public class UserDto {

	public static class UserCreateRequest {
		@NotBlank(message = "이메일을 입력하세요.")
		private String email;
		
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

		@NotBlank(message = "비밀번호를 입력하세요.")
		private String password;
		
		private String userRole;
		
		public UserCreateRequest(String email, String name, String password, String userRole) {
			this.email = email;
			this.name = name;
			this.password = password;
			this.userRole = userRole;
		}

		public UserCreateRequest() {}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getUserRole() {
			return userRole;
		}

		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			return "UserCreateRequest [email=" + email + ", name=" + name + ", password=" + password + ", userRole="
					+ userRole + "]";
		}
	}
	
	public static class UserSignInRequest {
		@NotBlank(message = "이메일을 입력하세요.")
		private String email;

		@NotBlank(message = "비밀번호를 입력하세요.")
		private String password;

		public UserSignInRequest(String email, String password) {
			this.email = email;
			this.password = password;
		}
		
		public UserSignInRequest() {}
		
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "UserSignIn [email=" + email + ", password=" + password + "]";
		}
	}
	
	public static class UserResponse {
		private Long id;
		private String email;
		private UserRole userRole;
		
		public UserResponse(Long id, String email, UserRole userRole) {
			this.id = id;
			this.email = email;
			this.userRole = userRole;
		}

		public UserResponse() {}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		public UserRole getUserRole() {
			return userRole;
		}

		public void setUserRole(UserRole userRole) {
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			return "UserResponse [id=" + id + ", email=" + email + ", userRole=" + userRole + "]";
		}
	}
}