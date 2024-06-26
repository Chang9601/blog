package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.common.type.JwtToken;
import com.whooa.blog.common.type.JwtType;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.type.UserRole;
import com.whooa.blog.utils.CookieBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* AuthenticationSuccessHandler 인터페이스는 인증 성공 시 상황을 처리한다. */
@Component
public class JwtAuthSuccessHandler implements AuthenticationSuccessHandler {
	private static Logger logger = LoggerFactory.getLogger(JwtAuthSuccessHandler.class);

	private ObjectMapper objectMapper;
	private JwtUtil jwtUtil;
	
	public JwtAuthSuccessHandler(ObjectMapper objectMapper, JwtUtil jwtUtil) {
		this.objectMapper = objectMapper;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			 Authentication authentication) throws IOException, ServletException {
		logger.info("JwtAuthSuccessHandler: 인증이 성공했습니다.");
		
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		
		Long id = userDetailsImpl.getId();
		String email = userDetailsImpl.getUsername();
		UserRole userRole = userDetailsImpl.getUserRole();

		JwtToken jwt = jwtUtil.issue(email);
		
		UserResponse userResponse = new UserResponse(id, email, userRole);
				
		ApiResponse<UserResponse> success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userResponse, new String[] {"로그인 되었습니다."});
		String serializedSuccess = objectMapper.writeValueAsString(success);

		response.addCookie(CookieBuilder.build(JwtType.ACCESS_TOKEN.getType(), jwt.getAccessToken(), true, 60*60, "Strict", "/"));
		response.addCookie(CookieBuilder.build(JwtType.REFRESH_TOKEN.getType(), jwt.getRefreshToken(), true, 60*60*24*30, "Strict", "/"));

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(serializedSuccess);
	}
}