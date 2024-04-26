package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.exception.UnauthorizedUserException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* AccessDeniedHandler 인터페이스는 사용자가 필요한 권한을 가지고 있지 않을 때 예외를 처리한다.*/
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
	private static Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);
	
	private ObjectMapper objectMapper;
	
	public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void handle(HttpServletRequest request,  HttpServletResponse response,
			 AccessDeniedException accessDeniedException) throws IOException, ServletException {
		logger.error("JwtAccessDeniedHandler: 필요한 권한이 없는 사용자입니다.");
		
		ApiResponse<UnauthorizedUserException> failure = ApiResponse.handleFailure(Code.FORBIDDEN.getCode(), Code.FORBIDDEN.getMessage(), null, new String[] {"필요한 권한이 없습니다."});
		String serializedFailure = objectMapper.writeValueAsString(failure);
		
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write(serializedFailure);
	}
}