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
			description = "사용자 이메일"
		)
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		private String email;
		
		@Schema(
			description = "사용자 이름"
		)		
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")		
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

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
		description = "사용자 수정 DTO(관리자)"
	)
	public static class UserAdminUpdateRequest {
		@Schema(
			description = "사용자 이메일"
		)
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		private String email;
		
		@Schema(
			description = "사용자 이름"
		)		
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")		
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

		@Schema(
			description = "사용자 비밀번호"
		)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$", message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.")
		private String password;
		
		@Schema(
			description = "사용자 역할"
		)		
		private String userRole;
		
		public UserAdminUpdateRequest(String email, String name, String password, String userRole) {
			this.email = email;
			this.name = name;
			this.password = password;
			this.userRole = userRole;
		}
		
		public UserAdminUpdateRequest() {}
		
		public UserAdminUpdateRequest email(String email) {
			this.email = email;
			return this;
		}
		
		public UserAdminUpdateRequest name(String name) {
			this.name = name;
			return this;
		}
		
		public UserAdminUpdateRequest password(String password) {
			this.password = password;
			return this;
		}

		public UserAdminUpdateRequest userRole(String userRole) {
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
			return "UserAdminUpdateRequest [email=" + email + ", name=" + name + ", password=" + password
					+ ", userRole=" + userRole + "]";
		}
	}

	@Schema(
		description = "사용자 수정 DTO"
	)
	public static class UserUpdateRequest {
		@Schema(
			description = "사용자 이메일"
		)
		@Email(message = "형식에 맞게 이메일을 입력하세요.")
		private String email;
		
		@Schema(
			description = "사용자 이름"
		)		
		@Size(min = 2, message = "이름은 최소 2자 이상입니다.")		
		@NotBlank(message = "이름을 입력하세요.")
		private String name;

		public UserUpdateRequest(String email, String name) {
			this.email = email;
			this.name = name;
		}

		public UserUpdateRequest() {}
		
		public UserUpdateRequest email(String email) {
			this.email = email;
			return this;
		}
		
		public UserUpdateRequest name(String name) {
			this.name = name;
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

		@Override
		public String toString() {
			return "UserUpdateRequest [email=" + email + ", name=" + name + "]";
		}
	}

	@Schema(
		description = "사용자 비밀번호 수정 DTO"
	)
	public static class UserPasswordUpdateRequest {
		@Schema(
			description = "사용자 새 비밀번호"
		)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$", message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.")
		private String newPassword;
		
		@Schema(
			description = "사용자 구 비밀번호"
		)
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,15}$", message = "비밀번호는 최소한 하나의 소문자(a-z), 하나의 대문자(A-Z), 하나의 숫자(0-9), 하나의 특수 문자(!@#$%^&*)를 포함해야 합니다.")
		private String oldPassword;
		
		public UserPasswordUpdateRequest(String newPassword, String oldPassword) {
			this.newPassword = newPassword;
			this.oldPassword = oldPassword;
		}

		public UserPasswordUpdateRequest() {}
		
		public UserPasswordUpdateRequest newPassword(String newPassword) {
			this.newPassword = newPassword;
			return this;
		}
		
		public UserPasswordUpdateRequest oldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
			return this;
		}

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