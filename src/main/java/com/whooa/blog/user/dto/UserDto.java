package com.whooa.blog.user.dto;

import org.hibernate.validator.constraints.Length;

import com.whooa.blog.user.type.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserDto {

	public static class UserCreateRequest {	
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		@Schema(description = "회원가입 시 필요한 사용자 이메일", example = "user1@naver.com", name = "이메일")
		private String email;
			
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)		
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "회원가입 시 필요한 사용자 이름", example = "사용자1", name = "이름")
		private String name;

		@Pattern(message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$")
		@Schema(description = "회원가입 시 필요한 사용자 비밀번호", example = "12341234aA!@", name = "비밀번호")
		private String password;

		@Schema(description = "회원가입 시 필요한 사용자 역할", example = "USER", name = "역할")
		private String userRole;

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

	public static class UserAdminUpdateRequest {
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		@Schema(description = "관리자 권한으로 사용자 수정 시 필요한 사용자 이메일", example = "user1@naver.com", name = "이메일")
		private String email;
			
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)		
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "관리자 권한으로 사용자 수정 시 필요한 사용자 이름", example = "사용자1", name = "이름")
		private String name;

		@Pattern(message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$")
		@Schema(description = "관리자 권한으로 사용자 수정 시 필요한 사용자 비밀번호", example = "12341234aA!@", name = "비밀번호")
		private String password;

		@Schema(description = "관리자 권한으로 사용자 수정 시 필요한 사용자 역할", example = "USER", name = "역할")
		private String userRole;
		
		public UserAdminUpdateRequest() {}
		
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
			return "UserAdminUpdateRequest [email=" + email + ", name=" + name + ", password=" + password
					+ ", userRole=" + userRole + "]";
		}
	}

	public static class UserPasswordUpdateRequest {
		@Pattern(message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$")
		@Schema(description = "비밀번호 수정 시 필요한 구 비밀번호", example = "12341234aA!@", name = "구 비밀번호")
		private String newPassword;
		
		@Pattern(message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.", regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$")
		@Schema(description = "비밀번호 수정 시 필요한 신 비밀번호", example = "43214321aA!@", name = "신 비밀번호")
		private String oldPassword;
		
		public UserPasswordUpdateRequest() {}
		
		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}
		
		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		@Override
		public String toString() {
			return "UserPasswordUpdateRequest [newPassword=" + newPassword + ", oldPassword=" + oldPassword + "]";
		}
	}

	public static class UserSearchRequest {			
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)		
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "회원검색 시 필요한 사용자 이름", example = "사용자1", name = "이름")
		private String name;
		
		public UserSearchRequest() {}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "UserUpdateRequest [name=" + name + "]";
		}
	}
	
	public static class UserUpdateRequest {
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		@Schema(description = "회원수정 시 필요한 사용자 이메일", example = "user1@naver.com", name = "이메일")
		private String email;
			
		@Length(message = "이름은 최소 2자 이상입니다.", min = 2)		
		@NotBlank(message = "이름을 입력하세요.")
		@Schema(description = "회원수정 시 필요한 사용자 이름", example = "사용자1", name = "이름")
		private String name;
		
		public UserUpdateRequest() {}
		
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

		@Override
		public String toString() {
			return "UserUpdateRequest [email=" + email + ", name=" + name + "]";
		}
	}
	
	public static class UserResponse {
		@Schema(description = "데이터베이스에 저장된 사용자 아이디", example = "1", name = "아이디")		
		private Long id;
		
		@Schema(description = "데이터베이스에 저장된 사용자 이메일", example = "user1@naver.com", name = "이메일")
		private String email;

		@Schema(description = "데이터베이스에 저장된 사용자 이름", example = "사용자1", name = "이름")
		private String name;
		
		@Schema(contentSchema = UserRole.class, description = "데이터베이스에 저장된 사용자 역할", name = "역할")
		private UserRole userRole;

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
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UserRole getUserRole() {
			return userRole;
		}

		public void setUserRole(UserRole userRole) {
			this.userRole = userRole;
		}

		@Override
		public String toString() {
			return "UserResponse [id=" + id + ", email=" + email + ", name=" + name + ", userRole=" + userRole + "]";
		}
	}
}