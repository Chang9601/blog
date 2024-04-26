package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* AuthenticationFailureHandler 인터페이스는 AccessDeniedException 인터페이스 및 AuthenticationEntryPoint 인터페이스에서 처리되지 않은 다른 인증 예외를 처리한다. */
@Component
public class JwtAuthFailureHandler implements AuthenticationFailureHandler {
	private static Logger logger = LoggerFactory.getLogger(JwtAuthFailureHandler.class);
	
	private ObjectMapper objectMapper;
	
	public JwtAuthFailureHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			 AuthenticationException exception) throws IOException, ServletException {
		logger.error("JwtAuthFailureHandler: 인증이 실패했습니다.");
		
		ApiResponse<?> failure;
		
		if (exception instanceof BadCredentialsException) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			failure = ApiResponse.handleFailure(Code.BAD_REQUEST.getCode(), Code.BAD_REQUEST.getMessage(), null, new String[] {"이메일 혹은 비밀번호가 유효하지 않습니다."});
		} else if (exception instanceof UsernameNotFoundException) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			failure = ApiResponse.handleFailure(Code.NOT_FOUND.getCode(), Code.NOT_FOUND.getMessage(), null, new String[] {"이메일과 일치하는 사용자가 존재하지 않습니다."});
		} else if (exception instanceof InternalAuthenticationServiceException) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			failure = ApiResponse.handleFailure(Code.INTERNAL_SERVER_ERROR.getCode(), Code.INTERNAL_SERVER_ERROR.getMessage(), null, new String[] {"시스템 오류가 발생했습니다."});
		} else {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			failure = ApiResponse.handleFailure(Code.INTERNAL_SERVER_ERROR.getCode(), Code.INTERNAL_SERVER_ERROR.getMessage(), null, new String[] {"알 수 없는 오류가 발생했습니다."});
		}
		
		String serializedFailure = objectMapper.writeValueAsString(failure);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(serializedFailure);
	}
}