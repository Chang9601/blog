package com.whooa.blog.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogoutHandlerImpl implements LogoutHandler {
	private UserRepository userRepository;

	public LogoutHandlerImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
			UserEntity userEntity;
			UserDetailsImpl userDetailsImpl;
			CookieUtil.clearJwtCookies(httpServletRequest, httpServletResponse);

			// TODO: authentication == null 오류 발생.
//			userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
//
//			userEntity = userRepository.findByIdAndActiveTrue(userDetailsImpl.getId()).get();
//			 
//			userEntity.setRefreshToken(null);
//			userRepository.save(userEntity);			
	}
}