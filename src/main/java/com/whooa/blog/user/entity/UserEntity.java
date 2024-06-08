package com.whooa.blog.user.entity;

import java.time.LocalDateTime;

import com.whooa.blog.common.entity.AbstractEntity;
import com.whooa.blog.user.type.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class UserEntity extends AbstractEntity {
	@Column(length = 300, nullable = false)
	private String name;
	
	@Column(length = 300, unique = true, nullable = false)
	private String email;
	
	@Column(length = 500, nullable = false)
	private String password;
	
	@Column(nullable = false)
	private Boolean active = true;
	
	@Column(name = "refresh_token", length = 500)
	private String refreshToken;
	
	@Column(name= "password_reset_token", length = 500)
	private String passwordResetToken;
	
	@Column(name = "password_reset_token_expiration")
	private LocalDateTime passwordResetTokenExpiration;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	private UserRole userRole = UserRole.USER;
	
	public UserEntity(Long id, String name, String email, String password, UserRole userRole) {
		super(id);
		
		this.name = name;
		this.email = email;
		this.password = password;
		this.userRole = userRole;
	}

	public UserEntity() {
		super(-1L);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getPasswordResetToken() {
		return passwordResetToken;
	}

	public void setPasswordResetToken(String passwordResetToken) {
		this.passwordResetToken = passwordResetToken;
	}

	public LocalDateTime getPasswordResetTokenExpiration() {
		return passwordResetTokenExpiration;
	}

	public void setPasswordResetTokenExpiration(LocalDateTime passwordResetTokenExpiration) {
		this.passwordResetTokenExpiration = passwordResetTokenExpiration;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + super.getId() + ", name=" + name + ", email=" + email + ", password=" + password + ", active=" + active
				+ ", refreshToken=" + refreshToken + ", passwordResetToken=" + passwordResetToken
				+ ", passwordResetTokenExpiration=" + passwordResetTokenExpiration + ", userRole=" + userRole + "]";
	}
}