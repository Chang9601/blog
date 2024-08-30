package com.whooa.blog.user.entity;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import com.whooa.blog.comment.entity.CommentEntity;
import com.whooa.blog.common.entity.CoreEntity;
import com.whooa.blog.common.security.oauth2.OAuth2Provider;
import com.whooa.blog.post.entity.PostEntity;
import com.whooa.blog.user.type.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserEntity extends CoreEntity {
	@Column(nullable = false)
	private Boolean active = true;
	
	@Column(length = 300, unique = true, nullable = false)
	private String email;
	
	@Column(length = 300, nullable = false)
	private String name;
	
	@Column(length = 500, nullable = false)
	private String password;
	
	@Column(name = "password_reset_token", length = 500)
	private String passwordResetToken;
	
	@Column(name = "password_reset_token_expiration")
	private LocalDateTime passwordResetTokenExpiration;
	
	@Column(name = "oauth2_access_token", length = 500)
	private String oAuth2AccessToken;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "oauth2_provider")
	private OAuth2Provider oAuth2Provider = OAuth2Provider.LOCAL;
	
	@Column(name = "oauth2_provider_id", length = 500)
	private String oAuth2ProviderId;
	
	@Column(name = "oauth2_refresh_token", length = 500)
	private String oAuth2RefreshToken;
	
	@Column(name = "refresh_token", length = 500)
	private String refreshToken;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	private UserRole userRole = UserRole.USER;

	@OneToMany(mappedBy = "user")
	private List<CommentEntity> comments = new ArrayList<CommentEntity>();
	
	@OneToMany(mappedBy = "user")
	private List<PostEntity> posts = new ArrayList<PostEntity>();

	public UserEntity() {
		super(0L);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
	
	public String getOAuth2AccessToken() {
		return oAuth2AccessToken;
	}

	public void setOAuth2AccessToken(String oAuth2AccessToken) {
		this.oAuth2AccessToken = oAuth2AccessToken;
	}

	public OAuth2Provider getOAuth2Provider() {
		return oAuth2Provider;
	}

	public void setOAuth2Provider(OAuth2Provider oAuth2Provider) {
		this.oAuth2Provider = oAuth2Provider;
	}

	public String getOAuth2ProviderId() {
		return oAuth2ProviderId;
	}

	public void setOAuth2ProviderId(String oAuth2ProviderId) {
		this.oAuth2ProviderId = oAuth2ProviderId;
	}
	
	public String getOAuth2RefreshToken() {
		return oAuth2RefreshToken;
	}

	public void setOAuth2RefreshToken(String oAuth2RefreshToken) {
		this.oAuth2RefreshToken = oAuth2RefreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public UserRole getUserRole() {
		return userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}
	
	public List<PostEntity> getPosts() {
		return posts;
	}

	public void setPosts(List<PostEntity> posts) {
		this.posts = posts;
	}
	
	public List<CommentEntity> getComments() {
		return comments;
	}

	public void setComments(List<CommentEntity> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + super.getId() + ", active=" + active + ", email=" + email + ", name=" + name + ", password=" + password
				+ ", passwordResetToken=" + passwordResetToken + ", passwordResetTokenExpiration="
				+ passwordResetTokenExpiration + "oAuth2AccessToken=" + oAuth2AccessToken + ", oAuth2Provider=" + oAuth2Provider + ", oAuth2ProviderId="
				+ oAuth2ProviderId + ", oAuth2RefreshToken=" + oAuth2RefreshToken + ", refreshToken=" + refreshToken
				+ ", userRole=" + userRole + ", comments=" + comments + ", posts=" + posts + "]";
	}
}