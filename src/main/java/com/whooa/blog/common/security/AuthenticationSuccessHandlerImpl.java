package com.whooa.blog.common.security;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.jwt.JwtBundle;
import com.whooa.blog.common.security.jwt.JwtType;
import com.whooa.blog.common.security.jwt.JwtUtil;
import com.whooa.blog.user.dto.UserDTO.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.util.CookieUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* AuthenticationSuccessHandler 인터페이스는 인증 성공 시 상황을 처리한다. */
@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
	private static Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class);

	private JwtUtil jwtUtil;
	private UserRepository userRepository;
	
	public AuthenticationSuccessHandlerImpl(JwtUtil jwtUtil, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			 Authentication authentication) throws IOException, ServletException {
		logger.info("[AuthenticationSuccessHandlerImpl] 인증이 성공했습니다.");
		
		String email;
		Long id;
		JwtBundle jwt;
		ApiResponse<UserResponse> success;
		UserDetailsImpl userDetailsImpl;
		UserEntity userEntity;
		UserResponse userResponse;
		UserRole userRole;

		userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		
		id = userDetailsImpl.getId();
		email = userDetailsImpl.getUsername();
		userRole = userDetailsImpl.getUserRole();

		jwt = jwtUtil.issue(email);
		userResponse = new UserResponse(id, email, userRole);
		success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userResponse, new String[] {"로그인 했습니다."});

		CookieUtil.set(httpServletResponse, JwtType.ACCESS_TOKEN.getType(), jwt.getAccessToken(), true, 60 * 60, "/", "Strict", false);
		CookieUtil.set(httpServletResponse, JwtType.REFRESH_TOKEN.getType(), jwt.getRefreshToken(), true, 60 * 60 * 24 * 30, "/", "Strict", false);
		
		userEntity = userRepository.findById(id).get();
		setRefreshToken(userEntity, jwt.getRefreshToken());

		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write(SerializeDeserializeUtil.serializeToString(success));
	}
	
	private void setRefreshToken(UserEntity userEntity, String refreshToken) {
		userEntity.refreshToken(refreshToken);
		userRepository.save(userEntity);
	}
}