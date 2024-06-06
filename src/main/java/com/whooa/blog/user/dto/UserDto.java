package com.whooa.blog.user.dto;

import com.whooa.blog.user.type.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDto {

	@Schema(
		description = "사용자 생성 DTO"
	)
	public static class UserCreateRequest {
		@Schema(
			description = "사용자 이름"
		)		
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")		
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

		@Schema(
			description = "사용자 이메일"
		)
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		private String email;

		@Schema(
			description = "사용자 비밀번호"
		)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$", message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.")
		private String password;

		@Schema(
			description = "사용자 역할"
		)		
		private String userRole;
		
		public UserCreateRequest(String email, String name, String password, String userRole) {
			this.email = email;
			this.name = name;
			this.password = password;
			this.userRole = userRole;
		}

		public UserCreateRequest() {}
		
		public UserCreateRequest email(String email) {
			this.email = email;
			return this;
		}
		
		public UserCreateRequest name(String name) {
			this.name = name;
			return this;
		}
		
		public UserCreateRequest password(String password) {
			this.password = password;
			return this;
		}
		
		public UserCreateRequest userRole(String userRole) {
			this.userRole = userRole;
			return this;
		}

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

	@Schema(
		description = "로그인 DTO"
	)	
	public static class UserSignInRequest {
		@Schema(
			description = "사용자 이메일"
		)	
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		private String email;

		@Schema(
			description = "사용자 비밀번호"
		)
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

	@Schema(
		description = "사용자 응답 DTO"
	)
	public static class UserResponse {
		@Schema(
			description = "사용자 아이디"
		)		
		private Long id;
		
		@Schema(
			description = "사용자 이메일"
		)
		private String email;
		
		@Schema(
			description = "사용자 역할"
		)
		private UserRole userRole;
		
		public UserResponse(Long id, String email, UserRole userRole) {
			this.id = id;
			this.email = email;
			this.userRole = userRole;
		}

		public UserResponse() {}
		
		public UserResponse email(String email) {
			this.email = email;
			return this;
		}
		
		public UserResponse userRole(UserRole userRole) {
			this.userRole = userRole;
			return this;
		}

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