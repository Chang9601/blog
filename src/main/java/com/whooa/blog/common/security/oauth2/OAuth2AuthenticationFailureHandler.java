package com.whooa.blog.common.security.oauth2;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.user.exception.UnauthenticatedUserException;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * OAuth2 인증 중 오류가 발생할 경우 SecurityConfig에 설정된 OAuth2AuthenticationFailureHandler의 onAuthenticationFailure() 메서드를 호출한다. 
 * 오류 메시지를 포함한 응답을 전송한다.
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	public OAuth2AuthenticationFailureHandler(
			HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
	}
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			AuthenticationException authenticationException) throws IOException, ServletException {
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequest(httpServletRequest, httpServletResponse);
		ApiResponse<UnauthenticatedUserException> failure;
		
		failure = ApiResponse.handleFailure(Code.OAUTH2_SIGNUP_FAILURE.getCode(), Code.OAUTH2_SIGNUP_FAILURE.getMessage(), null, new String[] {authenticationException.getLocalizedMessage()});

		httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write( SerializeDeserializeUtil.serializeToString(failure));
	}
}