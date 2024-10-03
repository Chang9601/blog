package com.whooa.blog.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.mapper.UserRoleMapper;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser mockCustomUser) {
		String email = mockCustomUser.email();
		String userRole = mockCustomUser.userRole();
				
		UserEntity userEntity = UserEntity.builder()
											.email(email)
											.name("사용자")
											.password("12345678Aa!@#$%")
											.userRole(UserRoleMapper.map(userRole))
											.build();
		
		UserDetailsImpl userDetailsImpl = new UserDetailsImpl(userEntity);
		
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities()));

		return securityContext;
	}
}