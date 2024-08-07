package com.whooa.blog.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.exception.UnauthorizedUserException;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* AccessDeniedHandler 인터페이스는 사용자가 필요한 권한을 가지고 있지 않을 때 예외를 처리한다.*/
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
	private static Logger logger = LoggerFactory.getLogger(AccessDeniedHandlerImpl.class);
		
	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			 AccessDeniedException accessDeniedException) throws IOException, ServletException {
		logger.error("[AccessDeniedHandlerImpl] 필요한 권한이 없는 사용자입니다.");
		
		ApiResponse<UnauthorizedUserException> failure = ApiResponse.handleFailure(Code.FORBIDDEN.getCode(), Code.FORBIDDEN.getMessage(), null, new String[] {"필요한 권한이 없습니다."});
		
		httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write(SerializeDeserializeUtil.serializeToString(failure));
	}
}