package com.whooa.blog.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
	private static Logger logger = LoggerFactory.getLogger(LogoutSuccessHandlerImpl.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication)
			throws IOException, ServletException {
		logger.info("[LogoutSuccessHandlerImpl] 로그아웃 성공했습니다.");

		ApiResponse<?> success;
		
		success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), null, new String[] {"로그아웃 했습니다."});

		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write(SerializeDeserializeUtil.serializeToString(success));
	}
}