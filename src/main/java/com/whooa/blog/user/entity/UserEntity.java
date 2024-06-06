package com.whooa.blog.user.entity;

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

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	@Override
	public String toString() {
		return "UserEntity [id=" + super.getId()  + ", name=" + name + ", email=" + email + ", password=" + password + ", userRole=" + userRole
				+ "]";
	}
}