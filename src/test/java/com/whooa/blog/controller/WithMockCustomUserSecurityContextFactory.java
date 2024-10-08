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
		String email, userRole;
		UserDetailsImpl userDetailsImpl;
		UserEntity userEntity;
		SecurityContext securityContext;
		
		email = mockCustomUser.email();
		userRole = mockCustomUser.userRole();
				
		userEntity = new UserEntity();
		userEntity.setEmail(email);
		userEntity.setName("사용자");
		userEntity.setPassword("12345678Aa!@#$%");
		userEntity.setUserRole(UserRoleMapper.map(userRole));

		userDetailsImpl = new UserDetailsImpl(userEntity);
		
		securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetailsImpl, null, userDetailsImpl.getAuthorities()));

		return securityContext;
	}
}