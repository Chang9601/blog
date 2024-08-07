package com.whooa.blog.common.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;

/* 
 * UserDetails는 UserDetailsService가 반환한다. 
 * DaoAuthenticationProvider는 UserDetails를 검증한 후 구성된 UserDetailsService가 반환한 UserDetails를 주체(principal)로 하는 Authentication을 반환한다. 
 */
public class UserDetailsImpl implements UserDetails, OAuth2User {
	private static final long serialVersionUID = 1L;
	
	private UserEntity userEntity;
    private Map<String, Object> attributes;

	public UserDetailsImpl(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
	public static UserDetailsImpl create(UserEntity userEntity) {
		return new UserDetailsImpl(userEntity);
	}
	
	public static UserDetailsImpl create(UserEntity userEntity, Map<String, Object> attributes) {
		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(userEntity);
		userDetailsImpl.setAttributes(attributes);
		
		return userDetailsImpl;
	}

	/*
	 * Spring Security는 GrantedAuthority 인터페이스의 구현체인 SimpleGrantedAuthority를 포함한다. 
	 * SimpleGrantedAuthority는 사용자가 지정한 문자열을 GrantedAuthority로 변환한다.
	 * 보안 아키텍처에 포함된 모든 AuthenticationProvider 인스턴스는 Authentication 객체를 채우기 위해 SimpleGrantedAuthority를 사용힌다.
	 * 기본적으로 역할 기반 권한 부여 규칙은 ROLE_이라는 접두사를 포함한다. 
	 * 권한 부여 규칙이 "USER" 역할을 요구하는 경우 Spring Security가 기본적으로 "ROLE_USER"를 반환하는 GrantedAuthority#getAuthority를 찾는다는 것을 의미한다. 
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority(userEntity.getUserRole().getRole()));
	}
	
	public Long getId() {
		return userEntity.getId();
	}
	
	public UserRole getUserRole() {
		return userEntity.getUserRole();
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}