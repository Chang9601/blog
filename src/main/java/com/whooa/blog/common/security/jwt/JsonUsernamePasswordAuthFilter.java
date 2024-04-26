package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Spring Security의 인증흐름 
 * 1. AbstractAuthenticationProcessingFilter 클래스에서 시작하면 기본적으로 form(x-www-form-urlencoded) 로그인을 지원한다.
 * 2. form 로그인을 활성화하면 AbstractAuthenticationProcessingFilter 클래스를 상속한 UsernamePasswordAuthenticationFilter 클래스가 추가된다.
 * 3. AbstractAuthenticationProcessingFilter 클래스는 HttpServletRequest 클래스에서 Authentication 객체를 생성하고 이를 AuthenticationManager 클래스에 전달한다.
 * 4. AuthenticationManager 클래스는 전달받은 Authentication 객체에서 인증을 시도하고 성공 시 SecurityContext 클래스에 Authentication 객체를 설정하고 실패 시 SecurityContext 클래스를 비운다.
 *
 * JsonUsernamePasswordAuthFilter 클래스는 JSON(application/json) 로그인을 지원한다.
 */
public class JsonUsernamePasswordAuthFilter extends AbstractAuthenticationProcessingFilter {
	
	public static String DEFAULT_JSON_USERNAME_KEY = "email";
	public static String DEFAULT_JSON_PASSWORD_KEY = "password";
	private static AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/auth/sign-in", "POST");

	private String usernameKey = DEFAULT_JSON_USERNAME_KEY;
	private String passwordKey = DEFAULT_JSON_PASSWORD_KEY;
	private boolean postOnly = true;
	private ObjectMapper objectMapper;

	public JsonUsernamePasswordAuthFilter(ObjectMapper objectMapper) {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
		
		this.objectMapper = objectMapper;
	}

	/* attemptAuthentication() 메서드는 요청에서 인증 정보를 가져오고 AuthenticationManager 클래스에 인증을 위임한다. */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		if (this.postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("지원되지 않는 인증 메서드입니다: " + request.getMethod());
		}
		
		if (request.getContentType() == null || !request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
			throw new AuthenticationServiceException("지원되지 않는 인증 MIME 타입입니다: " + request.getContentType());	
		}
		
		ServletInputStream inputStream = request.getInputStream();
		// TODO: 코드 개선으로 경고 삭제.
		Map<String, String> map = objectMapper.readValue(inputStream, Map.class);
		
		String username = obtainBody(usernameKey, map);
		String password = obtainBody(passwordKey, map);
		
		UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
		
		/* Allow subclasses to set the "details" property */
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	private String obtainBody(String key, Map<String, String> map) {
		String value = map.get(key);
		
		return Objects.isNull(value) ? "" : value.trim();
	}
	
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
	}

	public void setUsernameKey(String usernameKey) {
		Assert.hasText(usernameKey, "사용자 이름 키는 반드시 존재해야 합니다.");
		this.usernameKey = usernameKey;
	}


	public void setPasswordKey(String passwordKey) {
		Assert.hasText(passwordKey, "비밀번호 키는 반드시 존재해야 합니다.");
		this.passwordKey = passwordKey;
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public String getUsernameKey() {
		return this.usernameKey;
	}

	public String getPasswordKey() {
		return this.passwordKey;
	}
}