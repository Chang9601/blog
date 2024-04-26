package com.whooa.blog.user.dto;

import com.whooa.blog.user.type.UserRole;

import jakarta.validation.constraints.NotBlank;

public class UserDto {

	public static class UserCreateRequest {
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

		@NotBlank(message = "이메일을 입력하세요.")
		private String email;

		@NotBlank(message = "비밀번호를 입력하세요.")
		private String password;
		
		public UserCreateRequest(final String name, final String email, final String password) {
			this.name = name;
			this.email = email;
			this.password = password;
		}
		
		public UserCreateRequest() {}

		public final String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public final String getEmail() {
			return email;
		}

		public void setEmail(final String email) {
			this.email = email;
		}

		public final String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "UserCreateRequest [name=" + name + ", email=" + email + ", password=" + password + "]";
		}
	}
	
	public static class UserSignInRequest {
		@NotBlank(message = "이메일을 입력하세요.")
		private String email;

		@NotBlank(message = "비밀번호를 입력하세요.")
		private String password;

		public UserSignInRequest(final String email, final String password) {
			this.email = email;
			this.password = password;
		}
		
		public UserSignInRequest() {}
		
		public final String getEmail() {
			return email;
		}

		public void setEmail(final String email) {
			this.email = email;
		}

		public final String getPassword() {
			return password;
		}

		public void setPassword(final String password) {
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
		
		public UserResponse(final Long id, final String email, final UserRole userRole) {
			this.id = id;
			this.email = email;
			this.userRole = userRole;
		}

		public UserResponse() {}

		public final Long getId() {
			return id;
		}

		public void setId(final Long id) {
			this.id = id;
		}

		public final String getEmail() {
			return email;
		}

		public void setEmail(final String email) {
			this.email = email;
		}
		
		public final UserRole getUserRole() {
			return userRole;
		}

		public void setUserRole(final UserRole userRole) {
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			return "UserResponse [id=" + id + ", email=" + email + ", userRole=" + userRole + "]";
		}
	}
}