package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.exception.UnauthenticatedUserException;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * @Component 어노테이션은 설정 기반 어노테이션으로 클래스에 사용하면 Spring IoC 컨테이너가 컴포넌트 스캔 중에 자동으로 감지한다.
 * 컴포넌트 스캔이란 Spring IoC 컨테이너가 @Component 어노테이션이 지정된 클래스를 스캔하고 해당 클래스의 인스턴스를 생성하고 수명주기를 관리하는 것을 의미한다.
 * 
 * Spring Security는 최종적인 인증 및 인가 요청을 AuthorizationFilter 클래스에서 판단한다. 
 * 인증이 필요한 요청인데 인증이 되어 있지 않다거나, 필요한 권한이 존재하지 않는 요청을 AuthorizationDecision 클래스로 판단한다. 
 * 요건이 충족되지 않았다고 판단이 되면 예외를 던지고 이는 AuthorizationFilter 클래스의 바로 앞 필터인 ExceptionTranslationFilter 클래스에서 처리하게 된다.
 * 인증이 필요한 요청에 인증이 되지 않은 요청이라면 ExceptionTranslactionFilter 클래스는 AuthenticationEntryPoint 인터페이스의 메서드를 호출한다.
 * 
 * AuthenticationEntryPoint 인터페이스는 인증되지 않은 사용자가 인증이 필요한 자원에 접근하려고 시도할 때 예외를 처리한다.
 * 1. JWT가 존재하지 않는다.
 * 2. 유효한 JWT가 아니다(e.g., 토큰 만료).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

	// TODO: 경우에 따른 예외 처리.
	@Override
	public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			 AuthenticationException authenticationException) throws IOException, ServletException {
		logger.error("[JwtAuthEntryPoint] 인증되지 않은 사용자입니다.");
				
		ApiResponse<UnauthenticatedUserException> failure = ApiResponse.handleFailure(Code.UNAUTHORIZED.getCode(), Code.UNAUTHORIZED.getMessage(), null, new String[] {"로그인을 하셔야 합니다."});

		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write( SerializeDeserializeUtil.serialize(failure));
	}
}