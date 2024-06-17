package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private ObjectMapper objectMapper;

	public JwtLogoutSuccessHandler(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
			throws IOException, ServletException {
		ApiResponse<?> success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), null, new String[] {"로그아웃 했습니다."});
		String serializedSuccess = objectMapper.writeValueAsString(success);

		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write(serializedSuccess);
	}
}