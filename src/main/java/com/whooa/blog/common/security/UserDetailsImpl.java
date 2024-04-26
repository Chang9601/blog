package com.whooa.blog.common.security;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.type.UserRole;

/* 
 * UserDetailsService 인터페이스를 구현한 UserDetailService 클래스가 반환하는 클래스이다.
 * Spring Security는 인증 및 권한을 위해 UserDetail 클래스에 저장된 정보를 사용한다. 
 */
public class UserDetailsImpl implements UserDetails {
	private UserEntity userEntity;
	
	public UserDetailsImpl(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(new SimpleGrantedAuthority("USER"));
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
}